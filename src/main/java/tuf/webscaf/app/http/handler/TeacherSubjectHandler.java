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
import tuf.webscaf.app.dbContext.master.entity.TeacherSubjectEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSubjectRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "teacherSubjectHandler")
@Component
public class TeacherSubjectHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSubjectRepository teacherSubjectRepository;

    @Autowired
    SlaveTeacherSubjectRepository slaveTeacherSubjectRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_index")
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
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveTeacherSubjectEntity> slaveTeacherSubjectFlux = slaveTeacherSubjectRepository
                    .findAllByDeletedAtIsNullAndStatus(pageable, Boolean.valueOf(status));
            return slaveTeacherSubjectFlux
                    .collectList()
                    .flatMap(teacherSubjectEntity -> slaveTeacherSubjectRepository
                            .countByDeletedAtIsNullAndStatus(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherSubjectEntity> slaveTeacherSubjectFlux = slaveTeacherSubjectRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveTeacherSubjectFlux
                    .collectList()
                    .flatMap(teacherSubjectEntity -> slaveTeacherSubjectRepository
                            .countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (teacherSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSubjectRepository.findByUuidAndDeletedAtIsNull(teacherSubjectUUID)
                .flatMap(teacherSubjectEntity -> responseSuccessMsg("Record Fetched Successfully", teacherSubjectEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_store")
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

                    TeacherSubjectEntity entity = TeacherSubjectEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
                            .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
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

                    //   check teacher uuid exist
                    return teacherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherUUID())
                            //   check academic Session uuid exist
                            .flatMap(teacherEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicSessionUUID())
                                    //   check semester uuid exist
                                    .flatMap(academicSessionEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(entity.getSemesterUUID())
                                            //   check course subject uuid exist
                                            .flatMap(semesterEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(entity.getCourseSubjectUUID())
                                                    .flatMap(courseSubject -> teacherSubjectRepository.findFirstByTeacherUUIDAndCourseSubjectUUIDAndAcademicSessionUUIDAndDeletedAtIsNull
                                                                    (entity.getTeacherUUID(), entity.getCourseSubjectUUID(), entity.getAcademicSessionUUID())
                                                            .flatMap(checkRecord -> responseInfoMsg("Subject already Entered"))
                                                            .switchIfEmpty(Mono.defer(() -> teacherSubjectRepository.save(entity)
                                                                    .flatMap(teacherSubjectEntity -> responseSuccessMsg("Record Stored Successfully", teacherSubjectEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ))
                                                    ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
                                                    .onErrorResume(err -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Semester record does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Semester record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Academic Session record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Teacher record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherSubjectRepository.findByUuidAndDeletedAtIsNull(teacherSubjectUUID)
                        .flatMap(previousEntity -> {
                            TeacherSubjectEntity updatedEntity = TeacherSubjectEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                    .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
                                    .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
                                    .description(value.getFirst("description").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();
                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            //   check teacher uuid exist
                            return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                    //   check academic Session uuid exist
                                    .flatMap(teacherEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID())
                                            //   check semester uuid exist
                                            .flatMap(academicSessionEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSemesterUUID())
                                                    //   check course subject uuid exist
                                                    .flatMap(semesterEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseSubjectUUID())
                                                            .flatMap(courseSubject -> teacherSubjectRepository.findFirstByTeacherUUIDAndCourseSubjectUUIDAndAcademicSessionUUIDAndDeletedAtIsNullAndUuidIsNot
                                                                            (updatedEntity.getTeacherUUID(), updatedEntity.getCourseSubjectUUID(), updatedEntity.getAcademicSessionUUID(), teacherSubjectUUID)
                                                                    .flatMap(checkRecord -> responseInfoMsg("Subject already Entered"))
                                                                    .switchIfEmpty(Mono.defer(() -> teacherSubjectRepository.save(previousEntity)
                                                                            .then(teacherSubjectRepository.save(updatedEntity))
                                                                            .flatMap(teacherSubjectEntity -> responseSuccessMsg("Record Stored Successfully", teacherSubjectEntity))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ))
                                                            ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
                                                            .onErrorResume(err -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Semester record does not exist"))
                                                    .onErrorResume(err -> responseErrorMsg("Semester record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Academic Session record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Teacher record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return teacherSubjectRepository.findByUuidAndDeletedAtIsNull(teacherSubjectUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherSubjectEntity entity = TeacherSubjectEntity.builder()
                                        .uuid(val.getUuid())
                                        .teacherUUID(val.getTeacherUUID())
                                        .academicSessionUUID(val.getAcademicSessionUUID())
                                        .courseSubjectUUID(val.getCourseSubjectUUID())
                                        .semesterUUID(val.getSemesterUUID())
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

                                return teacherSubjectRepository.save(val)
                                        .then(teacherSubjectRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-subjects_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherSubjectRepository.findByUuidAndDeletedAtIsNull(teacherSubjectUUID)
                .flatMap(teacherSubjectEntity -> {
                            teacherSubjectEntity.setDeletedBy(UUID.fromString(userId));
                            teacherSubjectEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherSubjectEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherSubjectEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherSubjectEntity.setReqDeletedIP(reqIp);
                            teacherSubjectEntity.setReqDeletedPort(reqPort);
                            teacherSubjectEntity.setReqDeletedBrowser(reqBrowser);
                            teacherSubjectEntity.setReqDeletedOS(reqOs);
                            teacherSubjectEntity.setReqDeletedDevice(reqDevice);
                            teacherSubjectEntity.setReqDeletedReferer(reqReferer);

                            return teacherSubjectRepository.save(teacherSubjectEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }
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
