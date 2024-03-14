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
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingFinancialHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingFinancialHistoryRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSiblingFinancialHistoryHandler")
@Component
public class StudentSiblingFinancialHistoryHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingFinancialHistoryRepository studentSiblingFinancialHistoryRepository;

    @Autowired
    SlaveStudentSiblingFinancialHistoryRepository slaveStudentSiblingFinancialHistoryRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
        
        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Sibling UUID
        String studentSiblingUUID = serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !studentSiblingUUID.isEmpty()) {

            Flux<SlaveStudentSiblingFinancialHistoryEntity> slaveStudentSiblingFinancialHistoryFlux = slaveStudentSiblingFinancialHistoryRepository
                    .findAllByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status));

            return slaveStudentSiblingFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingFinancialHistoryEntity -> slaveStudentSiblingFinancialHistoryRepository
                            .countByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentSiblingFinancialHistoryEntity> slaveStudentSiblingFinancialHistoryFlux = slaveStudentSiblingFinancialHistoryRepository
                    .findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentSiblingFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingFinancialHistoryEntity -> slaveStudentSiblingFinancialHistoryRepository
                            .countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!studentSiblingUUID.isEmpty()) {

            Flux<SlaveStudentSiblingFinancialHistoryEntity> slaveStudentSiblingFinancialHistoryFlux = slaveStudentSiblingFinancialHistoryRepository
                    .findAllByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID));

            return slaveStudentSiblingFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingFinancialHistoryEntity -> slaveStudentSiblingFinancialHistoryRepository
                            .countByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID))
                            .flatMap(count -> {
                                if (studentSiblingFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveStudentSiblingFinancialHistoryEntity> slaveStudentSiblingFinancialHistoryFlux = slaveStudentSiblingFinancialHistoryRepository
                    .findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveStudentSiblingFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingFinancialHistoryEntity -> slaveStudentSiblingFinancialHistoryRepository
                            .countByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSiblingFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSiblingFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingFinancialHistoryUUID)
                .flatMap(addressEntity -> responseSuccessMsg("Record Fetched Successfully", addressEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }


    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Sibling Financial History
    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingFinancialHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {
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
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> {
                    StudentSiblingFinancialHistoryEntity entity = StudentSiblingFinancialHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description").trim())
                            .studentSiblingUUID(UUID.fromString(value.getFirst("studentSiblingUUID")))
                            .assetName(value.getFirst("assetName").trim())
                            .finance(Long.valueOf(value.getFirst("finance")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                            .reqCreatedIP(reqIp)
                            .reqCreatedPort(reqPort)
                            .reqCreatedBrowser(reqBrowser)
                            .reqCreatedOS(reqOs)
                            .reqCreatedDevice(reqDevice)
                            .reqCreatedReferer(reqReferer)
                            .build();

//                    check student sibling uuid exists
                    return studentSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSiblingUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentSiblingEntity -> studentSiblingFinancialHistoryRepository.save(entity)
                                                    .flatMap(studentSiblingFinancialHistoryEntity -> responseSuccessMsg("Record Stored Successfully", studentSiblingFinancialHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Sibling  record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Sibling  record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> studentSiblingFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingFinancialHistoryUUID)
                        .flatMap(entity -> {

                            StudentSiblingFinancialHistoryEntity updatedEntity = StudentSiblingFinancialHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .description(value.getFirst("description").trim())
                                    .studentSiblingUUID(entity.getStudentSiblingUUID())
                                    .assetName(value.getFirst("assetName").trim())
                                    .finance(Long.valueOf(value.getFirst("finance")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(entity.getReqCreatedIP())
                                    .reqCreatedPort(entity.getReqCreatedPort())
                                    .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                    .reqCreatedOS(entity.getReqCreatedOS())
                                    .reqCreatedDevice(entity.getReqCreatedDevice())
                                    .reqCreatedReferer(entity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //checks if currency uuid exists
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentSiblingEntity -> studentSiblingFinancialHistoryRepository.save(entity)
                                                    .then(studentSiblingFinancialHistoryRepository.save(updatedEntity))
                                                    .flatMap(studentSiblingFinancialHistoryEntity -> responseSuccessMsg("Record Updated Successfully", studentSiblingFinancialHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request"));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSiblingFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }
        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return studentSiblingFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingFinancialHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSiblingFinancialHistoryEntity entity = StudentSiblingFinancialHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentSiblingUUID(val.getStudentSiblingUUID())
                                        .assetName(val.getAssetName())
                                        .finance(val.getFinance())
                                        .currencyUUID(val.getCurrencyUUID())
                                        .description(val.getDescription())
                                        .status(status == true ? true : false)
                                        .createdAt(val.getCreatedAt())
                                        .createdBy(val.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(val.getReqCreatedIP())
                                        .reqCreatedPort(val.getReqCreatedPort())
                                        .reqCreatedBrowser(val.getReqCreatedBrowser())
                                        .reqCreatedOS(val.getReqCreatedOS())
                                        .reqCreatedDevice(val.getReqCreatedDevice())
                                        .reqCreatedReferer(val.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return studentSiblingFinancialHistoryRepository.save(val)
                                        .then(studentSiblingFinancialHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-financial-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return studentSiblingFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingFinancialHistoryUUID)
                .flatMap(stdSiblingFinancialHistoryEntityDB -> {

                    stdSiblingFinancialHistoryEntityDB.setDeletedBy(UUID.fromString(userId));
                    stdSiblingFinancialHistoryEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    stdSiblingFinancialHistoryEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    stdSiblingFinancialHistoryEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedIP(reqIp);
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedPort(reqPort);
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedBrowser(reqBrowser);
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedOS(reqOs);
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedDevice(reqDevice);
                    stdSiblingFinancialHistoryEntityDB.setReqDeletedReferer(reqReferer);

                    return studentSiblingFinancialHistoryRepository.save(stdSiblingFinancialHistoryEntityDB)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
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
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg
                )
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
