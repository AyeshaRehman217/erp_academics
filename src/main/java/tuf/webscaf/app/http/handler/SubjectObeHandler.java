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
import tuf.webscaf.app.dbContext.master.entity.SubjectObeEntity;
import tuf.webscaf.app.dbContext.master.repositry.CourseSubjectRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectObeCloPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectObeRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineOfferedRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectObeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectObeRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "subjectObeHandler")
@Component
public class SubjectObeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectObeRepository subjectObeRepository;

    @Autowired
    SlaveSubjectObeRepository slaveSubjectObeRepository;

    @Autowired
    SubjectObeCloPvtRepository subjectObeCloPvtRepository;

    @Autowired
    SubjectOutlineOfferedRepository subjectOutlineOfferedRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subject-obes_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Course Subject UUID
        String courseSubjectUUID = serverRequest.queryParam("courseSubjectUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));


        // if both status and course subject uuid are given
        if (!status.isEmpty() && !courseSubjectUUID.isEmpty()) {
            Flux<SlaveSubjectObeEntity> slaveSubjectObeFlux = slaveSubjectObeRepository
                    .findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status));

            return slaveSubjectObeFlux
                    .collectList()
                    .flatMap(subjectObeEntity -> slaveSubjectObeRepository
                            .countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(courseSubjectUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectObeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectObeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if course subject uuid is given
        else if (!courseSubjectUUID.isEmpty()) {
            Flux<SlaveSubjectObeEntity> slaveSubjectObeFlux = slaveSubjectObeRepository
                    .findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(courseSubjectUUID), searchKeyWord, UUID.fromString(courseSubjectUUID));

            return slaveSubjectObeFlux
                    .collectList()
                    .flatMap(subjectObeEntity -> slaveSubjectObeRepository
                            .countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(courseSubjectUUID), searchKeyWord, UUID.fromString(courseSubjectUUID))
                            .flatMap(count -> {
                                if (subjectObeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectObeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of status is present
        else if (!status.isEmpty()) {
            Flux<SlaveSubjectObeEntity> slaveSubjectObeFlux = slaveSubjectObeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveSubjectObeFlux
                    .collectList()
                    .flatMap(subjectObeEntity -> slaveSubjectObeRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectObeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectObeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter is not present
        else {
            Flux<SlaveSubjectObeEntity> slaveSubjectObeFlux = slaveSubjectObeRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveSubjectObeFlux
                    .collectList()
                    .flatMap(subjectObeEntity -> slaveSubjectObeRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectObeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectObeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obes_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectObeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectObeRepository.findByUuidAndDeletedAtIsNull(subjectObeUUID)
                .flatMap(subjectObeEntity -> responseSuccessMsg("Record Fetched Successfully", subjectObeEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obes_store")
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

                    SubjectObeEntity entity = SubjectObeEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
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

                    // check if name is unique
                    return subjectObeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(subjectObeName -> responseInfoMsg("Name already exist"))
                            .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(entity.getCourseSubjectUUID())
                                    .flatMap(courseSubjectEntity -> {

                                        // when course subject is obe
                                        if (courseSubjectEntity.getObe()) {
                                            return subjectObeRepository.save(entity)
                                                    .flatMap(subjectObeEntity -> responseSuccessMsg("Record Stored Successfully", subjectObeEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please Contact Developer"));
                                        }

                                        // else don't allow storing
                                        else {
                                            return responseInfoMsg("Course Subject must be OBE");
                                        }

                                    }).switchIfEmpty(responseInfoMsg("Course Subject Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course Subject Record does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_subject-obes_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectObeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectObeRepository.findByUuidAndDeletedAtIsNull(subjectObeUUID)
                        .flatMap(entity -> {

                            SubjectObeEntity updatedEntity = SubjectObeEntity.builder()
                                    .uuid(entity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
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

                            // check if name is unique
                            return subjectObeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), subjectObeUUID)
                                    .flatMap(subjectObeName -> responseInfoMsg("Name already exist"))
                                    .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseSubjectUUID())
                                            .flatMap(courseSubjectEntity -> {

                                                // when course subject is obe
                                                if (courseSubjectEntity.getObe()) {
                                                    return subjectObeRepository.save(entity)
                                                            .then(subjectObeRepository.save(updatedEntity))
                                                            .flatMap(subjectObeEntity -> responseSuccessMsg("Record Updated Successfully", subjectObeEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer"));
                                                }


                                                // else don't allow updating record
                                                else {
                                                    return responseInfoMsg("Course Subject must be OBE");
                                                }

                                            }).switchIfEmpty(responseInfoMsg("Course Subject Record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Course Subject Record does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obes_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectObeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectObeRepository.findByUuidAndDeletedAtIsNull(subjectObeUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectObeEntity entity = SubjectObeEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .courseSubjectUUID(val.getCourseSubjectUUID())
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

                                return subjectObeRepository.save(val)
                                        .then(subjectObeRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-obes_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectObeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectObeRepository.findByUuidAndDeletedAtIsNull(subjectObeUUID)
                // Checks if Subject Obe Reference exists in Subject Obe Clo Pvt
                .flatMap(subjectObeEntity -> subjectObeCloPvtRepository.findFirstBySubjectObeUUIDAndDeletedAtIsNull(subjectObeEntity.getUuid())
                        .flatMap(subjectObeCloPvtEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))
                        .switchIfEmpty(Mono.defer(() -> subjectOutlineOfferedRepository.findFirstBySubjectObeUUIDAndDeletedAtIsNull(subjectObeEntity.getUuid())
                                .flatMap(checkInSubjectOffered -> responseInfoMsg("Unable to delete Record as the Reference Exists"))
                        ))
                        .switchIfEmpty(Mono.defer(() -> {

                            subjectObeEntity.setDeletedBy(UUID.fromString(userId));
                            subjectObeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            subjectObeEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            subjectObeEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            subjectObeEntity.setReqDeletedIP(reqIp);
                            subjectObeEntity.setReqDeletedPort(reqPort);
                            subjectObeEntity.setReqDeletedBrowser(reqBrowser);
                            subjectObeEntity.setReqDeletedOS(reqOs);
                            subjectObeEntity.setReqDeletedDevice(reqDevice);
                            subjectObeEntity.setReqDeletedReferer(reqReferer);

                            return subjectObeRepository.save(subjectObeEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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
