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
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianFinancialHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianFinancialHistoryRepository;
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

@Tag(name = "teacherGuardianFinancialHistoryHandler")
@Component
public class TeacherGuardianFinancialHistoryHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianFinancialHistoryRepository teacherGuardianFinancialHistoryRepository;

    @Autowired
    SlaveTeacherGuardianFinancialHistoryRepository slaveTeacherGuardianFinancialHistoryRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Guardian UUID
        String teacherGuardianUUID = serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianFinancialHistoryEntity> slaveTeacherGuardianFinancialHistoryFlux = slaveTeacherGuardianFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status));

            return slaveTeacherGuardianFinancialHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianFinancialHistoryEntity -> slaveTeacherGuardianFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherGuardianFinancialHistoryEntity> slaveTeacherGuardianFinancialHistoryFlux = slaveTeacherGuardianFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveTeacherGuardianFinancialHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianFinancialHistoryEntity -> slaveTeacherGuardianFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianFinancialHistoryEntity> slaveTeacherGuardianFinancialHistoryFlux = slaveTeacherGuardianFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID));

            return slaveTeacherGuardianFinancialHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianFinancialHistoryEntity -> slaveTeacherGuardianFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID))
                            .flatMap(count -> {
                                if (teacherGuardianFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveTeacherGuardianFinancialHistoryEntity> slaveTeacherGuardianFinancialHistoryFlux = slaveTeacherGuardianFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveTeacherGuardianFinancialHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianFinancialHistoryEntity -> slaveTeacherGuardianFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherGuardianFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherGuardianFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFinancialHistoryUUID)
                .flatMap(studentGuardianFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Teacher Guardian Financial History
    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherGuardianFinancialHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_store")
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
                //checks if teacher guardian uuid exists
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                        .flatMap(teacherGuardianEntity -> {

                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Financial History. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                TeacherGuardianFinancialHistoryEntity entity = TeacherGuardianFinancialHistoryEntity.builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherGuardianUUID(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                                        .assetName(value.getFirst("assetName").trim())
                                        .finance(Long.valueOf(value.getFirst("finance")))
                                        .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                        .description(value.getFirst("description").trim())
                                        .status(Boolean.valueOf(value.getFirst("status")))
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

                                //checks if currency uuid exists
                                return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                        .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                .flatMap(currencyUUID -> teacherGuardianFinancialHistoryRepository.save(entity)
                                                        .flatMap(teacherGuardianFinancialHistoryEntity -> responseSuccessMsg("Record Stored Successfully", teacherGuardianFinancialHistoryEntity))
                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                                        ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherGuardianFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFinancialHistoryUUID)
                        .flatMap(entity -> {
                            TeacherGuardianFinancialHistoryEntity updatedEntity = TeacherGuardianFinancialHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherGuardianUUID(entity.getTeacherGuardianUUID())
                                    .assetName(value.getFirst("assetName").trim())
                                    .finance(Long.valueOf(value.getFirst("finance")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                    .description(value.getFirst("description").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
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
                                            .flatMap(currencyEntity -> teacherGuardianFinancialHistoryRepository.save(entity)
                                                    .then(teacherGuardianFinancialHistoryRepository.save(updatedEntity))
                                                    .flatMap(teacherGuardianFinancialHistoryEntity -> responseSuccessMsg("Record Updated Successfully", teacherGuardianFinancialHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherGuardianFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherGuardianFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFinancialHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherGuardianFinancialHistoryEntity entity = TeacherGuardianFinancialHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .teacherGuardianUUID(val.getTeacherGuardianUUID())
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

                                return teacherGuardianFinancialHistoryRepository.save(val)
                                        .then(teacherGuardianFinancialHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-financial-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianFinancialHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherGuardianFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFinancialHistoryUUID)
                .flatMap(teacherGuardianFinancialHistoryEntity -> {
                    teacherGuardianFinancialHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    teacherGuardianFinancialHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherGuardianFinancialHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherGuardianFinancialHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherGuardianFinancialHistoryEntity.setReqDeletedIP(reqIp);
                    teacherGuardianFinancialHistoryEntity.setReqDeletedPort(reqPort);
                    teacherGuardianFinancialHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    teacherGuardianFinancialHistoryEntity.setReqDeletedOS(reqOs);
                    teacherGuardianFinancialHistoryEntity.setReqDeletedDevice(reqDevice);
                    teacherGuardianFinancialHistoryEntity.setReqDeletedReferer(reqReferer);

                    return teacherGuardianFinancialHistoryRepository.save(teacherGuardianFinancialHistoryEntity)
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
