package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.EnrollmentStatusEntity;
import tuf.webscaf.app.dbContext.master.repositry.EnrollmentRepository;
import tuf.webscaf.app.dbContext.master.repositry.EnrollmentStatusRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveEnrollmentStatusEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveEnrollmentStatusRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Tag(name = "enrollmentStatusHandler")
@Component
public class EnrollmentStatusHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    EnrollmentStatusRepository enrollmentStatusRepository;

    @Autowired
    SlaveEnrollmentStatusRepository slaveEnrollmentStatusRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    ApiCallService apiCallService;
    @Value("${server.erp_drive_module.uri}")
    private String driveUri;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveEnrollmentStatusEntity> slaveCastEntityFlux = slaveEnrollmentStatusRepository
                    .findAllByReasonContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(enrollmentStatusEntityDB -> slaveEnrollmentStatusRepository
                            .countByReasonContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (enrollmentStatusEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentStatusEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveEnrollmentStatusEntity> slaveCastEntityFlux = slaveEnrollmentStatusRepository
                    .findAllByReasonContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(enrollmentStatusEntityDB -> slaveEnrollmentStatusRepository
                            .countByReasonContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (enrollmentStatusEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentStatusEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID enrollmentStatusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveEnrollmentStatusRepository.findByUuidAndDeletedAtIsNull(enrollmentStatusUUID)
                .flatMap(enrollmentStatusEntityDB -> responseSuccessMsg("Record Fetched Successfully.", enrollmentStatusEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_store")
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

                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                    EnrollmentStatusEntity enrollmentStatusEntity = EnrollmentStatusEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .reason(value.getFirst("reason"))
                            .colorCode(value.getFirst("colorCode"))
                            .attachmentUUID(UUID.fromString(value.getFirst("attachmentUUID")))
                            .approvedBy(UUID.fromString(value.getFirst("approvedBy")))
                            .enrollmentUUID(UUID.fromString(value.getFirst("enrollmentUUID")))
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

                    sendFormData.add("docId", String.valueOf(enrollmentStatusEntity.getAttachmentUUID()));

                    //  check enrollment uuid exist
                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(enrollmentStatusEntity.getEnrollmentUUID())
                            //  check document uuid exist
                            .flatMap(enrollmentEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", enrollmentStatusEntity.getAttachmentUUID())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            //fetch Document Extension from Document Json
                                            .flatMap(document -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {
                                                        //save Document Extension
                                                        enrollmentStatusEntity.setDocumentExtension(extension);
                                                        return enrollmentStatusRepository.save(enrollmentStatusEntity)
                                                                .flatMap(saveQuizEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", saveQuizEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                                    }).switchIfEmpty(responseInfoMsg("Unable to Fetch Document Extension."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch Document Extension.Please Contact Developer."))
                                            )).switchIfEmpty(responseInfoMsg("Unable to Upload Document."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                            ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Enrollment record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID enrollmentStatusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> enrollmentStatusRepository.findByUuidAndDeletedAtIsNull(enrollmentStatusUUID)
                        .flatMap(previousEnrollmentStatusEntity -> {

                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            EnrollmentStatusEntity updatedEnrollmentStatusEntity = EnrollmentStatusEntity.builder()
                                    .uuid(previousEnrollmentStatusEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .reason(value.getFirst("reason"))
                                    .colorCode(value.getFirst("colorCode"))
                                    .attachmentUUID(UUID.fromString(value.getFirst("attachmentUUID")))
                                    .approvedBy(UUID.fromString(value.getFirst("approvedBy")))
                                    .enrollmentUUID(UUID.fromString(value.getFirst("enrollmentUUID")))
                                    .createdAt(previousEnrollmentStatusEntity.getCreatedAt())
                                    .createdBy(previousEnrollmentStatusEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousEnrollmentStatusEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousEnrollmentStatusEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousEnrollmentStatusEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousEnrollmentStatusEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousEnrollmentStatusEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousEnrollmentStatusEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            sendFormData.add("docId", String.valueOf(updatedEnrollmentStatusEntity.getAttachmentUUID()));

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousEnrollmentStatusEntity.setDeletedBy(UUID.fromString(userId));
                            previousEnrollmentStatusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEnrollmentStatusEntity.setReqDeletedIP(reqIp);
                            previousEnrollmentStatusEntity.setReqDeletedPort(reqPort);
                            previousEnrollmentStatusEntity.setReqDeletedBrowser(reqBrowser);
                            previousEnrollmentStatusEntity.setReqDeletedOS(reqOs);
                            previousEnrollmentStatusEntity.setReqDeletedDevice(reqDevice);
                            previousEnrollmentStatusEntity.setReqDeletedReferer(reqReferer);

                            //  check enrollment uuid exist
                            return enrollmentRepository.findByUuidAndDeletedAtIsNull(updatedEnrollmentStatusEntity.getEnrollmentUUID())
                                    //  check document uuid exist
                                    .flatMap(enrollmentEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEnrollmentStatusEntity.getAttachmentUUID())
                                            .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                                    //fetch Document Extension from Document Json
                                                    .flatMap(document -> apiCallService.getDocumentExtension(documentJson)
                                                            .flatMap(extension -> {
                                                                //save Document Extension
                                                                updatedEnrollmentStatusEntity.setDocumentExtension(extension);
                                                                return enrollmentStatusRepository.save(previousEnrollmentStatusEntity)
                                                                        .then(enrollmentStatusRepository.save(updatedEnrollmentStatusEntity))
                                                                        .flatMap(saveQuizEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", saveQuizEntity)))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                                            }).switchIfEmpty(responseInfoMsg("Unable to Fetch Document Extension."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Fetch Document Extension.Please Contact Developer."))
                                                    )).switchIfEmpty(responseInfoMsg("Unable to Upload Document."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                    ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Enrollment record does not exist.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID enrollmentStatusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return enrollmentStatusRepository.findByUuidAndDeletedAtIsNull(enrollmentStatusUUID)
                            .flatMap(previousEnrollmentStatusEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEnrollmentStatusEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                EnrollmentStatusEntity updatedEnrollmentStatusEntity = EnrollmentStatusEntity.builder()
                                        .uuid(previousEnrollmentStatusEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .reason(previousEnrollmentStatusEntity.getReason())
                                        .colorCode(previousEnrollmentStatusEntity.getColorCode())
                                        .attachmentUUID(previousEnrollmentStatusEntity.getAttachmentUUID())
                                        .approvedBy(previousEnrollmentStatusEntity.getApprovedBy())
                                        .enrollmentUUID(previousEnrollmentStatusEntity.getEnrollmentUUID())
                                        .createdAt(previousEnrollmentStatusEntity.getCreatedAt())
                                        .createdBy(previousEnrollmentStatusEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEnrollmentStatusEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEnrollmentStatusEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEnrollmentStatusEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEnrollmentStatusEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEnrollmentStatusEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEnrollmentStatusEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEnrollmentStatusEntity.setDeletedBy(UUID.fromString(userId));
                                previousEnrollmentStatusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEnrollmentStatusEntity.setReqDeletedIP(reqIp);
                                previousEnrollmentStatusEntity.setReqDeletedPort(reqPort);
                                previousEnrollmentStatusEntity.setReqDeletedBrowser(reqBrowser);
                                previousEnrollmentStatusEntity.setReqDeletedOS(reqOs);
                                previousEnrollmentStatusEntity.setReqDeletedDevice(reqDevice);
                                previousEnrollmentStatusEntity.setReqDeletedReferer(reqReferer);

                                return enrollmentStatusRepository.save(previousEnrollmentStatusEntity)
                                        .then(enrollmentStatusRepository.save(updatedEnrollmentStatusEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollment-statuses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID enrollmentStatusUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return enrollmentStatusRepository.findByUuidAndDeletedAtIsNull(enrollmentStatusUUID)
                .flatMap(enrollmentStatusEntity -> {

                    enrollmentStatusEntity.setDeletedBy(UUID.fromString(userId));
                    enrollmentStatusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    enrollmentStatusEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    enrollmentStatusEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    enrollmentStatusEntity.setReqDeletedIP(reqIp);
                    enrollmentStatusEntity.setReqDeletedPort(reqPort);
                    enrollmentStatusEntity.setReqDeletedBrowser(reqBrowser);
                    enrollmentStatusEntity.setReqDeletedOS(reqOs);
                    enrollmentStatusEntity.setReqDeletedDevice(reqDevice);
                    enrollmentStatusEntity.setReqDeletedReferer(reqReferer);

                    return enrollmentStatusRepository.save(enrollmentStatusEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
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
