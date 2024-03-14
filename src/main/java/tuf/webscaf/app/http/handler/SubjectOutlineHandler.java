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
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectOutlineRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "subjectOutlineHandler")
@Component
public class SubjectOutlineHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @Autowired
    SlaveSubjectOutlineRepository slaveSubjectOutlineRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    SubjectOutlineAimRepository subjectOutlineAimRepository;

    @Autowired
    SubjectOutlineBookRepository subjectOutlineBookRepository;

    @Autowired
    TeacherOutlineRepository teacherOutlineRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    SubjectOutlineOfferedRepository subjectOutlineOfferedRepository;

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_index")
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
            Flux<SlaveSubjectOutlineEntity> slaveSubjectOutlineFlux = slaveSubjectOutlineRepository
                    .findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status));
            return slaveSubjectOutlineFlux
                    .collectList()
                    .flatMap(subjectOutlineEntity -> slaveSubjectOutlineRepository
                            .countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(courseSubjectUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseSubjectUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectOutlineEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if course subject uuid is given
        else if (!courseSubjectUUID.isEmpty()) {
            Flux<SlaveSubjectOutlineEntity> slaveSubjectOutlineFlux = slaveSubjectOutlineRepository
                    .findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(courseSubjectUUID), searchKeyWord, UUID.fromString(courseSubjectUUID));
            return slaveSubjectOutlineFlux
                    .collectList()
                    .flatMap(subjectOutlineEntity -> slaveSubjectOutlineRepository
                            .countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(courseSubjectUUID), searchKeyWord, UUID.fromString(courseSubjectUUID))
                            .flatMap(count -> {
                                if (subjectOutlineEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if status is given
        else if (!status.isEmpty()) {
            Flux<SlaveSubjectOutlineEntity> slaveSubjectOutlineFlux = slaveSubjectOutlineRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveSubjectOutlineFlux
                    .collectList()
                    .flatMap(subjectOutlineEntity -> slaveSubjectOutlineRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectOutlineEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if none of optional query parameter is present
        else {
            Flux<SlaveSubjectOutlineEntity> slaveSubjectOutlineFlux = slaveSubjectOutlineRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveSubjectOutlineFlux
                    .collectList()
                    .flatMap(subjectOutlineEntity -> slaveSubjectOutlineRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectOutlineEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectOutlineUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineUUID)
                .flatMap(subjectOutlineEntity -> responseSuccessMsg("Record Fetched Successfully", subjectOutlineEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_store")
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
                    SubjectOutlineEntity entity = SubjectOutlineEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID")))
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


                    // check if subject outline is unique
                    return subjectOutlineRepository.findFirstByNameIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(entity.getName(), entity.getCourseSubjectUUID())
                            .flatMap(subjectOutlineEntity -> responseInfoMsg("Name Already Exist"))
                            .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(entity.getCourseSubjectUUID())
                                    .flatMap(courseSubjectEntity -> {

                                        // when course subject is non-obe
                                        if (!courseSubjectEntity.getObe()) {
                                            return subjectOutlineRepository.save(entity)
                                                    .flatMap(subjectOutlineEntity -> responseSuccessMsg("Record Stored Successfully", subjectOutlineEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                        }

                                        // else don't allow storing
                                        else {
                                            return responseInfoMsg("Course Subject must be Non-OBE");
                                        }

                                    }).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectOutlineUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineUUID)
                        .flatMap(entity -> {

                            SubjectOutlineEntity updatedEntity = SubjectOutlineEntity.builder()
                                    .uuid(entity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID")))
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

                            // check if subject outline is unique
                            return subjectOutlineRepository.findFirstByNameIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), updatedEntity.getCourseSubjectUUID(), subjectOutlineUUID)
                                    .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(entity.getCourseSubjectUUID())
                                            .flatMap(courseSubjectEntity -> {

                                                // when course subject is non-obe
                                                if (!courseSubjectEntity.getObe()) {
                                                    return subjectOutlineRepository.save(entity)
                                                            .then(subjectOutlineRepository.save(updatedEntity))
                                                            .flatMap(subjectOutlineEntity -> responseSuccessMsg("Record Updated Successfully", subjectOutlineEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                }


                                                // else don't allow updating record
                                                else {
                                                    return responseInfoMsg("Course Subject must be Non-OBE");
                                                }

                                            }).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectOutlineUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectOutlineEntity entity = SubjectOutlineEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .description(val.getDescription())
                                        .courseSubjectUUID(val.getCourseSubjectUUID())
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


                                return subjectOutlineRepository.save(val)
                                        .then(subjectOutlineRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outlines_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectOutlineUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineUUID)
                //Checks if Subject Outline Reference exists in Subject Outline Aims
                .flatMap(subjectOutlineEntity -> subjectOutlineAimRepository.findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(subjectOutlineEntity.getUuid())
                        .flatMap(subjectOutlineAimEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))
                        //Checks if Subject Outline Reference exists in Subject Outline Books
                        .switchIfEmpty(Mono.defer(() -> subjectOutlineBookRepository.findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(subjectOutlineEntity.getUuid())
                                .flatMap(subjectOutlineBookEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))))
                        //Checks if Subject Outline Reference exists in Subject Outline Offered
                        .switchIfEmpty(Mono.defer(() -> subjectOutlineOfferedRepository.findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(subjectOutlineEntity.getUuid())
                                .flatMap(subjectOutlineOfferedEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            subjectOutlineEntity.setDeletedBy(UUID.fromString(userId));
                            subjectOutlineEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            subjectOutlineEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            subjectOutlineEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            subjectOutlineEntity.setReqDeletedIP(reqIp);
                            subjectOutlineEntity.setReqDeletedPort(reqPort);
                            subjectOutlineEntity.setReqDeletedBrowser(reqBrowser);
                            subjectOutlineEntity.setReqDeletedOS(reqOs);
                            subjectOutlineEntity.setReqDeletedDevice(reqDevice);
                            subjectOutlineEntity.setReqDeletedReferer(reqReferer);

                            return subjectOutlineRepository.save(subjectOutlineEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
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
