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
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherNationalityPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.NationalityRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherNationalityPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveNationalityRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherNationalityPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "teacherFatherNationalityPvtHandler")
@Component
public class TeacherFatherNationalityPvtHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherNationalityPvtRepository teacherFatherNationalityPvtRepository;

    @Autowired
    SlaveTeacherFatherNationalityPvtRepository slaveTeacherFatherNationalityPvtRepository;

    @Autowired
    SlaveNationalityRepository slaveNationalityRepository;

    @Autowired
    NationalityRepository nationalityRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-father-nationalities_existing_show")
    public Mono<ServerResponse> showNationalitiesAgainstTeacherFather(ServerRequest serverRequest) {

        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("teacherFatherUUID"));

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty()) {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .existingTeacherFatherNationalitiesListWithStatus(teacherFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                    .flatMap(teacherFatherEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherFatherNationalityRecordsWithStatus(teacherFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Father  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Father  Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .existingTeacherFatherNationalitiesList(teacherFatherUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                    .flatMap(teacherFatherEntity -> slaveNationalitiesFlux.collectList()
                            .flatMap(nationalityEntity -> slaveNationalityRepository.countExistingTeacherFatherNationalityRecords(teacherFatherUUID, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (nationalityEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Teacher Father  Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Teacher Father  Record does not exist. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-nationalities_mapped_show")
    public Mono<ServerResponse> showMappedNationalitiesAgainstTeacherFather(ServerRequest serverRequest) {

        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("teacherFatherUUID"));

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

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .showTeacherFatherNationalitiesListWithStatus(teacherFatherUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherFatherNationalitiesWithStatus(teacherFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));

        } else {
            Flux<SlaveNationalityEntity> slaveNationalitiesFlux = slaveNationalityRepository
                    .showTeacherFatherNationalitiesList(teacherFatherUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveNationalitiesFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countMappedTeacherFatherNationalities(teacherFatherUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-nationalities_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("teacherFatherUUID"));

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
                .flatMap(value -> teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                        .flatMap(teacherFatherEntity -> {

                            //getting List of Nationalities From Front
                            List<String> listOfNationalityUUID = new LinkedList<>(value.get("nationalityUUID"));

                            listOfNationalityUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getNationalityUUID : listOfNationalityUUID) {
                                l_list.add(UUID.fromString(getNationalityUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return nationalityRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingNationalities -> {
                                            // Nationality UUID List
                                            List<UUID> nationalityList = new ArrayList<>();

                                            for (NationalityEntity nationality : existingNationalities) {
                                                nationalityList.add(nationality.getUuid());
                                            }

                                            if (!nationalityList.isEmpty()) {

                                                // nationality uuid list to show in response
                                                List<UUID> nationalityRecords = new ArrayList<>(nationalityList);

                                                List<TeacherFatherNationalityPvtEntity> listPvt = new ArrayList<>();

                                                return teacherFatherNationalityPvtRepository.findAllByTeacherFatherUUIDAndNationalityUUIDInAndDeletedAtIsNull(teacherFatherUUID, nationalityList)
                                                        .collectList()
                                                        .flatMap(teacherFatherPvtEntity -> {
                                                            for (TeacherFatherNationalityPvtEntity pvtEntity : teacherFatherPvtEntity) {
                                                                //Removing Existing Nationality UUID in Nationality Final List to be saved that does not contain already mapped values
                                                                nationalityList.remove(pvtEntity.getNationalityUUID());
                                                            }

                                                            // iterate Nationality UUIDs for given Teacher Father
                                                            for (UUID nationalityUUID : nationalityList) {
                                                                TeacherFatherNationalityPvtEntity teacherFatherNationalityPvtEntity = TeacherFatherNationalityPvtEntity
                                                                        .builder()
                                                                        .nationalityUUID(nationalityUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .teacherFatherUUID(teacherFatherUUID)
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
                                                                listPvt.add(teacherFatherNationalityPvtEntity);
                                                            }

                                                            return teacherFatherNationalityPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!nationalityList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", nationalityRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseInfoMsg("Record Already Exists", nationalityRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Nationality Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Nationality Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Nationality Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Nationality First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Father Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Teacher Father Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-nationalities_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("teacherFatherUUID"));
        UUID nationalityUUID = UUID.fromString(serverRequest.queryParam("nationalityUUID").map(String::toString).orElse(""));
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

        return nationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                .flatMap(nationalityEntity -> teacherFatherNationalityPvtRepository
                        .findFirstByTeacherFatherUUIDAndNationalityUUIDAndDeletedAtIsNull(teacherFatherUUID, nationalityUUID)
                        .flatMap(teacherFatherNationalityPvtEntity -> {
                            teacherFatherNationalityPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherFatherNationalityPvtEntity.setDeletedBy(UUID.fromString(userId));
                            teacherFatherNationalityPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherFatherNationalityPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherFatherNationalityPvtEntity.setReqDeletedIP(reqIp);
                            teacherFatherNationalityPvtEntity.setReqDeletedPort(reqPort);
                            teacherFatherNationalityPvtEntity.setReqDeletedBrowser(reqBrowser);
                            teacherFatherNationalityPvtEntity.setReqDeletedOS(reqOs);
                            teacherFatherNationalityPvtEntity.setReqDeletedDevice(reqDevice);
                            teacherFatherNationalityPvtEntity.setReqDeletedReferer(reqReferer);

                            return teacherFatherNationalityPvtRepository.save(teacherFatherNationalityPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", nationalityEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Nationality does not exist"))
                .onErrorResume(err -> responseErrorMsg("Nationality does not exist.Please Contact Developer."));

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
