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
import tuf.webscaf.app.dbContext.master.entity.*;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentRepository;
import tuf.webscaf.app.dbContext.master.repositry.PeoRepository;
import tuf.webscaf.app.dbContext.master.repositry.PloPeoPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.PloRepository;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloPeoPvtDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlavePeoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlavePloPeoPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.StringVerifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "ploPeoPvtHandler")
public class PloPeoPvtHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    PloPeoPvtRepository ploPeoPvtRepository;

    @Autowired
    SlavePloPeoPvtRepository slavePloPeoPvtRepository;

    @Autowired
    SlavePeoRepository slavePeoRepository;

    @Autowired
    PeoRepository peoRepository;

    @Autowired
    PloRepository ploRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_plo-peos_index")
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
            Flux<SlavePloPeoPvtDto> slavePeoFlux = slavePloPeoPvtRepository
                    .indexWithDepartment(UUID.fromString(departmentUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePloPeoPvtRepository
                            .countPloPeoRecordsWithDepartment(UUID.fromString(departmentUUID), searchKeyWord)
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
            Flux<SlavePloPeoPvtDto> slavePeoFlux = slavePloPeoPvtRepository
                    .index(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePloPeoPvtRepository.countPloPeoRecords(searchKeyWord)
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

    @AuthHasPermission(value = "academic_api_v1_plo-peos_un-mapped_show")
    public Mono<ServerResponse> showUnMappedPeosAgainstPlo(ServerRequest serverRequest) {

        UUID ploUUID = UUID.fromString(serverRequest.pathVariable("ploUUID"));

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // department Query Parameter
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

        if (!departmentUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlavePeoDto> slavePeosFlux = slavePeoRepository
                    .showUnMappedPloPeoListWithStatusAndDepartment(UUID.fromString(departmentUUID), ploUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeosFlux
                    .collectList()
                    .flatMap(peoEntity -> slavePeoRepository.countUnMappedPloPeoRecordsWithStatusAndDepartment(UUID.fromString(departmentUUID), ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (peoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", peoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!departmentUUID.isEmpty()) {
            Flux<SlavePeoDto> slavePeosFlux = slavePeoRepository
                    .showUnMappedPloPeoListAgainstDepartment(UUID.fromString(departmentUUID), ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeosFlux
                    .collectList()
                    .flatMap(peoEntity -> slavePeoRepository.countUnMappedPloPeoRecordsWithDepartment(UUID.fromString(departmentUUID), ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (peoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", peoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlavePeoDto> slavePeosFlux = slavePeoRepository
                    .showUnMappedPloPeoListWithStatus(ploUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeosFlux
                    .collectList()
                    .flatMap(peoEntity -> slavePeoRepository.countUnMappedPloPeoRecordsWithStatus(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (peoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", peoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlavePeoDto> slavePeosFlux = slavePeoRepository
                    .showUnMappedPloPeoList(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeosFlux
                    .collectList()
                    .flatMap(peoEntity -> slavePeoRepository.countUnMappedPloPeoRecords(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (peoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", peoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }


    @AuthHasPermission(value = "academic_api_v1_plo-peos_mapped_show")
    public Mono<ServerResponse> showMappedPeoAgainstPlo(ServerRequest serverRequest) {

        UUID ploUUID = UUID.fromString(serverRequest.pathVariable("ploUUID"));

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // department Query Parameter
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

        if (!status.isEmpty() && !departmentUUID.isEmpty()) {
            Flux<SlavePeoDto> slavePeoFlux = slavePeoRepository
                    .showMappedPloPeoListWithStatusAndDepartment(UUID.fromString(departmentUUID), ploUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord,
                            searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePeoRepository
                            .countMappedPloPeoListWithStatusAndDepartment(UUID.fromString(departmentUUID), ploUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (ploEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ploEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        } else if (!departmentUUID.isEmpty()) {
            Flux<SlavePeoDto> slavePeoFlux = slavePeoRepository
                    .showMappedPloPeoListAgainstDepartment(UUID.fromString(departmentUUID), ploUUID, searchKeyWord, searchKeyWord,
                            searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePeoRepository
                            .countMappedPloPeoListWithDepartment(UUID.fromString(departmentUUID), ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (ploEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", ploEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlavePeoDto> slavePeoFlux = slavePeoRepository
                    .showMappedPloPeoListWithStatus(ploUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord,
                            searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePeoRepository
                            .countMappedPloPeoWithStatus(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
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
            Flux<SlavePeoDto> slavePeoFlux = slavePeoRepository
                    .showMappedPloPeoList(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoFlux
                    .collectList()
                    .flatMap(ploEntity -> slavePeoRepository.countMappedPloPeo(ploUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
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

    @AuthHasPermission(value = "academic_api_v1_plo-peos_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        UUID ploUUID = UUID.fromString(serverRequest.pathVariable("ploUUID"));

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
                .flatMap(value -> ploRepository.findByUuidAndDeletedAtIsNull(ploUUID)
                        .flatMap(ploEntity -> {

                            //getting List of Peo's From Front
                            List<String> listOfPeo = new LinkedList<>(value.get("peoUUID"));

                            listOfPeo.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getPeo : listOfPeo) {
                                l_list.add(UUID.fromString(getPeo));
                            }

                            if (!l_list.isEmpty()) {
                                return peoRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(showUnMappedPeo -> {
                                            // Student UUID List
                                            List<UUID> peoList = new ArrayList<>();

                                            for (PeoEntity peo : showUnMappedPeo) {
                                                peoList.add(peo.getUuid());
                                            }

                                            if (!peoList.isEmpty()) {

                                                List<UUID> departmentUUIDList = new ArrayList<>();

                                                //iterate over peo entity and get department uuid's
                                                for (PeoEntity peo : showUnMappedPeo) {
                                                    //check if department uuid's list does not contain uuid before then add in list
                                                    if (!departmentUUIDList.contains(peo.getDepartmentUUID())) {
                                                        departmentUUIDList.add(peo.getDepartmentUUID());
                                                    }
                                                }

                                                //check if the section campus course matches with peos campus courses
                                                if (departmentUUIDList.size() > 1) {
                                                    return responseInfoMsg("PEO cannot be added in list as the department of all PEO does not matches");
                                                } else {
                                                    //if peo list contains 1 uuid then match with section campus course either same or not
                                                    if (departmentUUIDList.size() == 1 && !departmentUUIDList.contains(ploEntity.getDepartmentUUID())) {
                                                        return responseInfoMsg("Peo's department Does not matches with Plo's Department");
                                                    } else {
                                                        //peo uuid list to show in response
                                                        List<UUID> peoFinalRecords = new ArrayList<>(peoList);

                                                        List<PloPeoPvtEntity> listPvt = new ArrayList<>();

                                                        return ploPeoPvtRepository.findAllByPloUUIDAndPeoUUIDInAndDeletedAtIsNull(ploUUID, peoList)
                                                                .collectList()
                                                                .flatMap(ploPeoPvtEntity -> {

                                                                    for (PloPeoPvtEntity pvtEntity : ploPeoPvtEntity) {
                                                                        //Removing UnMapped Peo UUID in Student Final List to be saved that does not contain already mapped values
                                                                        peoList.remove(pvtEntity.getPeoUUID());
                                                                    }

                                                                    // iterate Student UUIDs for given Section
                                                                    for (UUID peoUUID : peoList) {
                                                                        PloPeoPvtEntity ploPeoPvt = PloPeoPvtEntity
                                                                                .builder()
                                                                                .peoUUID(peoUUID)
                                                                                .uuid(UUID.randomUUID())
                                                                                .ploUUID(ploUUID)
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

                                                                        listPvt.add(ploPeoPvt);
                                                                    }

                                                                    return ploPeoPvtRepository.saveAll(listPvt)
                                                                            .collectList()
                                                                            .flatMap(groupList -> {

                                                                                if (!peoList.isEmpty()) {
                                                                                    return responseSuccessMsg("Record Stored Successfully", peoFinalRecords)
                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                } else {
                                                                                    return responseSuccessMsg("Record Already Exists", peoFinalRecords);
                                                                                }

                                                                            }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                                });
                                                    }
                                                }
                                            } else {
                                                return responseInfoMsg("Peo Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Peo's Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Peo's Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Peo's First");
                            }
                        }).switchIfEmpty(responseInfoMsg("PLO Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("PLO Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_plo-peos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID ploUUID = UUID.fromString(serverRequest.pathVariable("ploUUID"));
        UUID peoUUID = UUID.fromString(serverRequest.queryParam("peoUUID").map(String::toString).orElse("").trim());
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

        return peoRepository.findByUuidAndDeletedAtIsNull(peoUUID)
                .flatMap(peoEntity -> ploPeoPvtRepository
                        .findFirstByPloUUIDAndPeoUUIDAndDeletedAtIsNull(ploUUID, peoUUID)
                        .flatMap(ploPeoPvtEntity -> {

                            ploPeoPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            ploPeoPvtEntity.setDeletedBy(UUID.fromString(userId));
                            ploPeoPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            ploPeoPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            ploPeoPvtEntity.setReqDeletedIP(reqIp);
                            ploPeoPvtEntity.setReqDeletedPort(reqPort);
                            ploPeoPvtEntity.setReqDeletedBrowser(reqBrowser);
                            ploPeoPvtEntity.setReqDeletedOS(reqOs);
                            ploPeoPvtEntity.setReqDeletedDevice(reqDevice);
                            ploPeoPvtEntity.setReqDeletedReferer(reqReferer);

                            return ploPeoPvtRepository.save(ploPeoPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", peoEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Peo Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Peo Record does not exist.Please Contact Developer."));

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
