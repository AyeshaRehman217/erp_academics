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
import tuf.webscaf.app.dbContext.master.entity.StudentMotherFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherFinancialHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherFinancialHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherFinancialHistoryRepository;
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

@Tag(name = "studentMotherFinancialHistoryHandler")
@Component
public class StudentMotherFinancialHistoryHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherFinancialHistoryRepository studentMotherFinancialHistoryRepository;

    @Autowired
    SlaveStudentMotherFinancialHistoryRepository slaveStudentMotherFinancialHistoryRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student UUID
        String studentMotherUUID = serverRequest.queryParam("studentMotherUUID").map(String::toString).orElse("").trim();

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !studentMotherUUID.isEmpty()) {

            Flux<SlaveStudentMotherFinancialHistoryEntity> slaveStudentMotherFinancialHistoryFlux = slaveStudentMotherFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status));

            return slaveStudentMotherFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentMotherFinancialHistoryEntity -> slaveStudentMotherFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentMotherFinancialHistoryEntity> slaveStudentMotherFinancialHistoryFlux = slaveStudentMotherFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentMotherFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentMotherFinancialHistoryEntity -> slaveStudentMotherFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!studentMotherUUID.isEmpty()) {

            Flux<SlaveStudentMotherFinancialHistoryEntity> slaveStudentMotherFinancialHistoryFlux = slaveStudentMotherFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID));

            return slaveStudentMotherFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentMotherFinancialHistoryEntity -> slaveStudentMotherFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID))
                            .flatMap(count -> {
                                if (studentMotherFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveStudentMotherFinancialHistoryEntity> slaveStudentMotherFinancialHistoryFlux = slaveStudentMotherFinancialHistoryRepository
                    .findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveStudentMotherFinancialHistoryFlux
                    .collectList()
                    .flatMap(studentMotherFinancialHistoryEntity -> slaveStudentMotherFinancialHistoryRepository.countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentMotherFinancialHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherFinancialHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }
    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentMotherFinancialHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentMotherFinancialHistoryUUID)
                .flatMap(studentMotherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Mother Financial History
    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherFinancialHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_store")
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
                    StudentMotherFinancialHistoryEntity entity = StudentMotherFinancialHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentMotherUUID(UUID.fromString(value.getFirst("studentMotherUUID")))
                            .assetName(value.getFirst("assetName"))
                            .finance(Long.valueOf(value.getFirst("finance")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                            .description(value.getFirst("description"))
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

                    //checks if student mother profile uuid exists
                    return studentMotherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMotherUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentMotherEntity -> studentMotherFinancialHistoryRepository.save(entity)
                                                    .flatMap(studentMotherFinancialHistoryEntity -> responseSuccessMsg("Record Stored Successfully", studentMotherFinancialHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Mother record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Mother record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentMotherFinancialHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentMotherFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentMotherFinancialHistoryUUID)
                        .flatMap(entity -> {

                            StudentMotherFinancialHistoryEntity updatedEntity = StudentMotherFinancialHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentMotherUUID(entity.getStudentMotherUUID())
                                    .assetName(value.getFirst("assetName"))
                                    .finance(Long.valueOf(value.getFirst("finance")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                                    .description(value.getFirst("description"))
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
                                            .flatMap(studentMotherEntity -> studentMotherFinancialHistoryRepository.save(entity)
                                                    .then(studentMotherFinancialHistoryRepository.save(updatedEntity))
                                                    .flatMap(studentMotherFinancialHistoryEntity -> responseSuccessMsg("Record Updated Successfully", studentMotherFinancialHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentMotherFinancialHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentMotherFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentMotherFinancialHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentMotherFinancialHistoryEntity entity = StudentMotherFinancialHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentMotherUUID(val.getStudentMotherUUID())
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

                                return studentMotherFinancialHistoryRepository.save(val)
                                        .then(studentMotherFinancialHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-financial-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentMotherFinancialHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentMotherFinancialHistoryRepository.findByUuidAndDeletedAtIsNull(studentMotherFinancialHistoryUUID)
                .flatMap(studentMotherFinancialHistoryEntity -> {

                    studentMotherFinancialHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    studentMotherFinancialHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentMotherFinancialHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentMotherFinancialHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentMotherFinancialHistoryEntity.setReqDeletedIP(reqIp);
                    studentMotherFinancialHistoryEntity.setReqDeletedPort(reqPort);
                    studentMotherFinancialHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    studentMotherFinancialHistoryEntity.setReqDeletedOS(reqOs);
                    studentMotherFinancialHistoryEntity.setReqDeletedDevice(reqDevice);
                    studentMotherFinancialHistoryEntity.setReqDeletedReferer(reqReferer);

                    return studentMotherFinancialHistoryRepository.save(studentMotherFinancialHistoryEntity)
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
}
