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
import tuf.webscaf.app.dbContext.master.entity.GuardianTypeEntity;
import tuf.webscaf.app.dbContext.master.repositry.GuardianTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherProfileRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveGuardianTypeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveGuardianTypeRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.SlugifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "guardianTypeHandler")
@Component
public class GuardianTypeHandler {
    @Autowired
    CustomResponse appresponse;
    @Autowired
    GuardianTypeRepository guardianTypeRepository;
    @Autowired
    SlaveGuardianTypeRepository slaveGuardianTypeRepository;
    @Autowired
    StudentProfileRepository studentProfileRepository;
    @Autowired
    TeacherProfileRepository teacherProfileRepository;
    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;
    @Autowired
    SlugifyHelper slugifyHelper;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_guardian-type_index")
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
            Flux<SlaveGuardianTypeEntity> slaveCastEntityFlux = slaveGuardianTypeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), pageable);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(guardianTypeEntityDB -> slaveGuardianTypeRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (guardianTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", guardianTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveGuardianTypeEntity> slaveCastEntityFlux = slaveGuardianTypeRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, pageable);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(guardianTypeEntityDB -> slaveGuardianTypeRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (guardianTypeEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", guardianTypeEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_guardian-type_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID guardianTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveGuardianTypeRepository.findByUuidAndDeletedAtIsNull(guardianTypeUUID)
                .flatMap(guardianTypeEntityDB -> responseSuccessMsg("Record Fetched Successfully.", guardianTypeEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_guardian-type_store")
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

                    GuardianTypeEntity guardianTypeEntity = GuardianTypeEntity.builder()
                            .name(value.getFirst("name"))
                            .slug(slugifyHelper.slugify(value.getFirst("name")))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description"))
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

                    // check if guardian type name is unique
                    return guardianTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(guardianTypeEntity.getName())
                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                            // check if guardian type slug is unique
                            .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findFirstBySlugAndDeletedAtIsNull(guardianTypeEntity.getSlug())
                                    .flatMap(checkSlugMsg -> responseInfoMsg("Slug Already Exists"))))
                            .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.save(guardianTypeEntity)
                                    .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully.", saveEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_guardian-type_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID guardianTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(guardianTypeUUID)
                        .flatMap(previousGuardianTypeEntity -> {

                            GuardianTypeEntity updatedGuardianTypeEntity = GuardianTypeEntity.builder()
                                    .name(value.getFirst("name"))
                                    .slug(slugifyHelper.slugify(value.getFirst("name")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .description(value.getFirst("description"))
                                    .uuid(previousGuardianTypeEntity.getUuid())
                                    .createdAt(previousGuardianTypeEntity.getCreatedAt())
                                    .createdBy(previousGuardianTypeEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousGuardianTypeEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousGuardianTypeEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousGuardianTypeEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousGuardianTypeEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousGuardianTypeEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousGuardianTypeEntity.getReqCreatedReferer())
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
                            previousGuardianTypeEntity.setDeletedBy(UUID.fromString(userId));
                            previousGuardianTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousGuardianTypeEntity.setReqDeletedIP(reqIp);
                            previousGuardianTypeEntity.setReqDeletedPort(reqPort);
                            previousGuardianTypeEntity.setReqDeletedBrowser(reqBrowser);
                            previousGuardianTypeEntity.setReqDeletedOS(reqOs);
                            previousGuardianTypeEntity.setReqDeletedDevice(reqDevice);
                            previousGuardianTypeEntity.setReqDeletedReferer(reqReferer);

                            // check if guardian type name is unique
                            return guardianTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedGuardianTypeEntity.getName(), guardianTypeUUID)
                                    .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                    // check if guardian type slug is unique
                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(updatedGuardianTypeEntity.getSlug(), guardianTypeUUID)
                                            .flatMap(checkSlugMsg -> responseInfoMsg("Slug Already Exists"))))
                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.save(previousGuardianTypeEntity)
                                            .then(guardianTypeRepository.save(updatedGuardianTypeEntity))
                                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully.", saveEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_guardian-type_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID guardianTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return guardianTypeRepository.findByUuidAndDeletedAtIsNull(guardianTypeUUID)
                            .flatMap(previousGuardianTypeEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousGuardianTypeEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                GuardianTypeEntity updatedGuardianTypeEntity = GuardianTypeEntity.builder()
                                        .name(previousGuardianTypeEntity.getName())
                                        .slug(previousGuardianTypeEntity.getSlug())
                                        .status(status == true ? true : false)
                                        .description(previousGuardianTypeEntity.getDescription())
                                        .uuid(previousGuardianTypeEntity.getUuid())
                                        .createdAt(previousGuardianTypeEntity.getCreatedAt())
                                        .createdBy(previousGuardianTypeEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousGuardianTypeEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousGuardianTypeEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousGuardianTypeEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousGuardianTypeEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousGuardianTypeEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousGuardianTypeEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousGuardianTypeEntity.setDeletedBy(UUID.fromString(userId));
                                previousGuardianTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousGuardianTypeEntity.setReqDeletedIP(reqIp);
                                previousGuardianTypeEntity.setReqDeletedPort(reqPort);
                                previousGuardianTypeEntity.setReqDeletedBrowser(reqBrowser);
                                previousGuardianTypeEntity.setReqDeletedOS(reqOs);
                                previousGuardianTypeEntity.setReqDeletedDevice(reqDevice);
                                previousGuardianTypeEntity.setReqDeletedReferer(reqReferer);

                                return guardianTypeRepository.save(previousGuardianTypeEntity)
                                        .then(guardianTypeRepository.save(updatedGuardianTypeEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_guardian-type_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID guardianTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return guardianTypeRepository.findByUuidAndDeletedAtIsNull(guardianTypeUUID)
                //Checks if Guardian Type Reference exists in Teacher Guardian
                .flatMap(guardianTypeEntity -> teacherGuardianRepository.findFirstByGuardianTypeUUIDAndDeletedAtIsNull(guardianTypeEntity.getUuid())
                        .flatMap(teacherSiblingProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> {

                            guardianTypeEntity.setDeletedBy(UUID.fromString(userId));
                            guardianTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            guardianTypeEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            guardianTypeEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            guardianTypeEntity.setReqDeletedIP(reqIp);
                            guardianTypeEntity.setReqDeletedPort(reqPort);
                            guardianTypeEntity.setReqDeletedBrowser(reqBrowser);
                            guardianTypeEntity.setReqDeletedOS(reqOs);
                            guardianTypeEntity.setReqDeletedDevice(reqDevice);
                            guardianTypeEntity.setReqDeletedReferer(reqReferer);

                            return guardianTypeRepository.save(guardianTypeEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
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
