package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CloEntity;
import tuf.webscaf.app.dbContext.master.entity.SubjectObeCloPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.CloRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectObeCloPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectObeRepository;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectObeCloPvtDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectObePvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "subjectObeCloPvtHandler")
@Component
public class SubjectObeCloPvtHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectObeCloPvtRepository subjectObeCloPvtRepository;

    @Autowired
    SlaveSubjectObePvtRepository slaveSubjectObeCloPvtRepository;

    @Autowired
    SlaveCloRepository slaveCloRepository;

    @Autowired
    CloRepository cloRepository;

    @Autowired
    SubjectObeRepository subjectObeRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subject-obe-clos_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        // Query Parameter of department UUID
        String departmentUUID = serverRequest.queryParam("departmentUUID").map(String::toString).orElse("").trim();

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!departmentUUID.isEmpty()) {
            Flux<SlaveSubjectObeCloPvtDto> slavePeoFlux = slaveSubjectObeCloPvtRepository
                    .indexWithDepartment(UUID.fromString(departmentUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slaveSubjectObeCloPvtRepository
                            .countSubjectObeCloRecordsWithDepartment(UUID.fromString(departmentUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (ploEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ploEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        } else {
            Flux<SlaveSubjectObeCloPvtDto> slavePeoFlux = slaveSubjectObeCloPvtRepository
                    .index(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slaveSubjectObeCloPvtRepository.countSubjectObeCloRecords(searchKeyWord)
                            .flatMap(count -> {
                                if (ploEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ploEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        }
    }

    /**
     * This Function is used to fetch Unmapped Clos against Subject OBE with & Without Status Filter
     **/
    @AuthHasPermission(value = "academic_api_v1_subject-obe-clos_un-mapped_show")
    public Mono<ServerResponse> showClosAgainstSubjectObe(ServerRequest serverRequest) {

        final UUID subjectObeUUID = UUID.fromString(serverRequest.pathVariable("subjectObeUUID"));

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        Optional<String> status = serverRequest.queryParam("status");

        if (status.isPresent()) {
            Flux<SlaveCloDto> slaveClosFlux = slaveSubjectObeCloPvtRepository
                    .unMappedClosListWithStatus(subjectObeUUID, Boolean.valueOf(status.get()), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClosFlux
                    .collectList()
                    .flatMap(cloEntityDB -> slaveCloRepository.countUnMappedCloSubjectObeRecordsWithStatus(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status.get()))
                            .flatMap(count -> {
                                if (cloEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", cloEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        } else {
            Flux<SlaveCloDto> slaveClosFlux = slaveSubjectObeCloPvtRepository
                    .unMappedClosList(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClosFlux
                    .collectList()
                    .flatMap(cloEntityDB -> slaveCloRepository.countUnMappedCloSubjectObeRecords(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (cloEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", cloEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        }
    }

    /**
     * This Function is used to fetch Mapped Clos against Subject OBE with & Without Status Filter
     **/
    @AuthHasPermission(value = "academic_api_v1_subject-obe-clos_mapped_show")
    public Mono<ServerResponse> showMappedClosAgainstSubjectObe(ServerRequest serverRequest) {

        final UUID subjectObeUUID = UUID.fromString(serverRequest.pathVariable("subjectObeUUID"));

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        Optional<String> status = serverRequest.queryParam("status");

        if (status.isPresent()) {
            Flux<SlaveCloDto> slaveClosFlux = slaveSubjectObeCloPvtRepository
                    .showClosListWithStatus(subjectObeUUID, Boolean.valueOf(status.get()), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClosFlux
                    .collectList()
                    .flatMap(cloEntityDB -> slaveCloRepository.countMappedCloSubjectObeRecordsWithStatus(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status.get()))
                            .flatMap(count -> {
                                if (cloEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", cloEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        } else {
            Flux<SlaveCloDto> slaveClosFlux = slaveSubjectObeCloPvtRepository
                    .showClosList(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClosFlux
                    .collectList()
                    .flatMap(cloEntityDB -> slaveCloRepository.countMappedCloSubjectObeRecords(subjectObeUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (cloEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records fetched successfully", cloEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obe-clos_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");
        final UUID subjectObeUUID = UUID.fromString(serverRequest.pathVariable("subjectObeUUID"));

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return serverRequest.formData()
                .flatMap(value -> subjectObeRepository.findByUuidAndDeletedAtIsNull(subjectObeUUID)
                        .flatMap(subjectObeEntity -> {

                            //getting List of Clos From Front
                            List<String> listOfCloUUID = new LinkedList<>(value.get("cloUUID"));

                            listOfCloUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getCloUUID : listOfCloUUID) {
                                l_list.add(UUID.fromString(getCloUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return cloRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(unMappedClos -> {
                                            // Clo UUID List
                                            List<UUID> cloList = new ArrayList<>();

                                            for (CloEntity clo : unMappedClos) {
                                                cloList.add(clo.getUuid());
                                            }

                                            if (!cloList.isEmpty()) {

                                                // clo uuid list to show in response
                                                List<UUID> cloRecords = new ArrayList<>(cloList);

                                                List<SubjectObeCloPvtEntity> listPvt = new ArrayList<>();

                                                return subjectObeCloPvtRepository.findAllBySubjectObeUUIDAndCloUUIDInAndDeletedAtIsNull(subjectObeUUID, cloList)
                                                        .collectList()
                                                        .flatMap(subjectObePvtEntity -> {
                                                            for (SubjectObeCloPvtEntity pvtEntity : subjectObePvtEntity) {
                                                                //Removing UnMapped Clo UUID in Clo Final List to be saved that does not contain already mapped values
                                                                cloList.remove(pvtEntity.getCloUUID());
                                                            }

                                                            // iterate Clo UUIDs for given Subject Obe
                                                            for (UUID cloUUID : cloList) {

                                                                SubjectObeCloPvtEntity subjectObeCloPvtEntity = SubjectObeCloPvtEntity
                                                                        .builder()
                                                                        .cloUUID(cloUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .subjectObeUUID(subjectObeUUID)
                                                                        .createdBy(UUID.fromString(userId))
                                                                        .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                        .reqCreatedIP(reqIp)
                                                                        .reqCreatedPort(reqPort)
                                                                        .reqCreatedBrowser(reqBrowser)
                                                                        .reqCreatedOS(reqOs)
                                                                        .reqCreatedDevice(reqDevice)
                                                                        .reqCreatedReferer(reqReferer)
                                                                        .build();

                                                                listPvt.add(subjectObeCloPvtEntity);
                                                            }

                                                            return subjectObeCloPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!cloList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", cloRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", cloRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Clo Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Clo Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Clo Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Clo First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Subject Obe Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Subject Obe Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obe-clos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID subjectObeUUID = UUID.fromString(serverRequest.pathVariable("subjectObeUUID"));
        UUID cloUUID = UUID.fromString(serverRequest.queryParam("cloUUID").map(String::toString).orElse(""));

        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return cloRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                .flatMap(cloEntity -> subjectObeCloPvtRepository
                        .findFirstBySubjectObeUUIDAndCloUUIDAndDeletedAtIsNull(subjectObeUUID, cloUUID)
                        .flatMap(subjectObePvtEntity -> {

                            subjectObePvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            subjectObePvtEntity.setDeletedBy(UUID.fromString(userId));
                            subjectObePvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            subjectObePvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            subjectObePvtEntity.setReqDeletedIP(reqIp);
                            subjectObePvtEntity.setReqDeletedPort(reqPort);
                            subjectObePvtEntity.setReqDeletedBrowser(reqBrowser);
                            subjectObePvtEntity.setReqDeletedOS(reqOs);
                            subjectObePvtEntity.setReqDeletedDevice(reqDevice);
                            subjectObePvtEntity.setReqDeletedReferer(reqReferer);

                            return subjectObeCloPvtRepository.save(subjectObePvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", cloEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Clo does not exist"))
                .onErrorResume(err -> responseErrorMsg("Clo does not exist.Please Contact Developer."));
    }

    public Mono<ServerResponse> responseInfoMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );


        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()

        );
    }

    public Mono<ServerResponse> responseInfoMsg(String msg, Object entity) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );


        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                entity

        );
    }

    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.empty()

        );
    }


    public Mono<ServerResponse> responseErrorMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.ERROR,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }

    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg)
        );


        return appresponse.set(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }
}
