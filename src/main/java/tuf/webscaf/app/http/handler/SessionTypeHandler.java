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
import tuf.webscaf.app.dbContext.master.entity.SessionTypeEntity;
import tuf.webscaf.app.dbContext.master.repositry.AcademicSessionRepository;
import tuf.webscaf.app.dbContext.master.repositry.SessionTypeRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSessionTypeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSessionTypeRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "sessionTypeHandler")
@Component
public class SessionTypeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    SessionTypeRepository sessionTypeRepository;

    @Autowired
    SlaveSessionTypeRepository slaveSessionTypeRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_session-types_index")
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

        // Query Parameter of isSpecial
        String isSpecial = serverRequest.queryParam("isSpecial").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if both status and isSpecial query params are present
        if (!status.isEmpty() && !isSpecial.isEmpty()) {
            Flux<SlaveSessionTypeEntity> slaveSessionTypeEntityFlux = slaveSessionTypeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(status), Boolean.valueOf(isSpecial), searchKeyWord, Boolean.valueOf(status), Boolean.valueOf(isSpecial), pageable);
            return slaveSessionTypeEntityFlux
                    .collectList()
                    .flatMap(sessionTypeEntityDB -> slaveSessionTypeRepository
                            .countByNameContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), Boolean.valueOf(isSpecial), searchKeyWord, Boolean.valueOf(status), Boolean.valueOf(isSpecial))
                            .flatMap(count ->
                            {
                                if (sessionTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sessionTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only isSpecial is present
        else if (!isSpecial.isEmpty()) {
            Flux<SlaveSessionTypeEntity> slaveSessionTypeEntityFlux = slaveSessionTypeRepository
                    .findAllByNameContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(isSpecial), searchKeyWord, Boolean.valueOf(isSpecial), pageable);
            return slaveSessionTypeEntityFlux
                    .collectList()
                    .flatMap(sessionTypeEntityDB -> slaveSessionTypeRepository
                            .countByNameContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(isSpecial), searchKeyWord, Boolean.valueOf(isSpecial))
                            .flatMap(count ->
                            {
                                if (sessionTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sessionTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only status is present
        else if (!status.isEmpty()) {
            Flux<SlaveSessionTypeEntity> slaveSessionTypeEntityFlux = slaveSessionTypeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), pageable);
            return slaveSessionTypeEntityFlux
                    .collectList()
                    .flatMap(sessionTypeEntityDB -> slaveSessionTypeRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (sessionTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sessionTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if none of query params are present
        else {
            Flux<SlaveSessionTypeEntity> slaveSessionTypeEntityFlux = slaveSessionTypeRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, pageable);
            return slaveSessionTypeEntityFlux
                    .collectList()
                    .flatMap(sessionTypeEntityDB -> slaveSessionTypeRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (sessionTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sessionTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_session-types_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID sessionTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSessionTypeRepository.findByUuidAndDeletedAtIsNull(sessionTypeUUID)
                .flatMap(sessionTypeEntityDB -> responseSuccessMsg("Record Fetched Successfully", sessionTypeEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_session-types_store")
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

                    SessionTypeEntity sessionTypeEntity = SessionTypeEntity.builder()
                            .name(value.getFirst("name"))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description"))
                            .isSpecial(Boolean.valueOf(value.getFirst("isSpecial")))
                            .uuid(UUID.randomUUID())
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

                    // check session type name is unique
                    return sessionTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(sessionTypeEntity.getName())
                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.save(sessionTypeEntity)
                                    .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_session-types_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID sessionTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> sessionTypeRepository.findByUuidAndDeletedAtIsNull(sessionTypeUUID)
                        .flatMap(previousSessionTypeEntity -> {

                            SessionTypeEntity updatedSessionTypeEntity = SessionTypeEntity.builder()
                                    .name(value.getFirst("name"))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .description(value.getFirst("description"))
                                    .isSpecial(Boolean.valueOf(value.getFirst("isSpecial")))
                                    .uuid(previousSessionTypeEntity.getUuid())
                                    .createdAt(previousSessionTypeEntity.getCreatedAt())
                                    .createdBy(previousSessionTypeEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousSessionTypeEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousSessionTypeEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousSessionTypeEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousSessionTypeEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousSessionTypeEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousSessionTypeEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousSessionTypeEntity.setDeletedBy(UUID.fromString(userId));
                            previousSessionTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousSessionTypeEntity.setReqDeletedIP(reqIp);
                            previousSessionTypeEntity.setReqDeletedPort(reqPort);
                            previousSessionTypeEntity.setReqDeletedBrowser(reqBrowser);
                            previousSessionTypeEntity.setReqDeletedOS(reqOs);
                            previousSessionTypeEntity.setReqDeletedDevice(reqDevice);
                            previousSessionTypeEntity.setReqDeletedReferer(reqReferer);

                            // check session type name is unique
                            return sessionTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedSessionTypeEntity.getName(), sessionTypeUUID)
                                    .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.save(previousSessionTypeEntity)
                                            .then(sessionTypeRepository.save(updatedSessionTypeEntity))
                                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_session-types_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID sessionTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return sessionTypeRepository.findByUuidAndDeletedAtIsNull(sessionTypeUUID)
                            .flatMap(previousSessionTypeEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousSessionTypeEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SessionTypeEntity updatedSessionTypeEntity = SessionTypeEntity.builder()
                                        .name(previousSessionTypeEntity.getName())
                                        .status(status == true ? true : false)
                                        .description(previousSessionTypeEntity.getDescription())
                                        .isSpecial(previousSessionTypeEntity.getIsSpecial())
                                        .uuid(previousSessionTypeEntity.getUuid())
                                        .createdAt(previousSessionTypeEntity.getCreatedAt())
                                        .createdBy(previousSessionTypeEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousSessionTypeEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousSessionTypeEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousSessionTypeEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousSessionTypeEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousSessionTypeEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousSessionTypeEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousSessionTypeEntity.setDeletedBy(UUID.fromString(userId));
                                previousSessionTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousSessionTypeEntity.setReqDeletedIP(reqIp);
                                previousSessionTypeEntity.setReqDeletedPort(reqPort);
                                previousSessionTypeEntity.setReqDeletedBrowser(reqBrowser);
                                previousSessionTypeEntity.setReqDeletedOS(reqOs);
                                previousSessionTypeEntity.setReqDeletedDevice(reqDevice);
                                previousSessionTypeEntity.setReqDeletedReferer(reqReferer);

                                return sessionTypeRepository.save(previousSessionTypeEntity)
                                        .then(sessionTypeRepository.save(updatedSessionTypeEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status There is something wrong please try again."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_session-types_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID sessionTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return sessionTypeRepository.findByUuidAndDeletedAtIsNull(sessionTypeUUID)
                .flatMap(sessionTypeEntity -> academicSessionRepository.findFirstBySessionTypeUUIDAndDeletedAtIsNull(sessionTypeUUID)
                        .flatMap(academicSessionEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> {

                            sessionTypeEntity.setDeletedBy(UUID.fromString(userId));
                            sessionTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            sessionTypeEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            sessionTypeEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            sessionTypeEntity.setReqDeletedIP(reqIp);
                            sessionTypeEntity.setReqDeletedPort(reqPort);
                            sessionTypeEntity.setReqDeletedBrowser(reqBrowser);
                            sessionTypeEntity.setReqDeletedOS(reqOs);
                            sessionTypeEntity.setReqDeletedDevice(reqDevice);
                            sessionTypeEntity.setReqDeletedReferer(reqReferer);

                            return sessionTypeRepository.save(sessionTypeEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
