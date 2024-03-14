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
import tuf.webscaf.app.dbContext.master.dto.AttendanceDto;
import tuf.webscaf.app.dbContext.master.entity.AttendanceEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAttendanceDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAttendanceEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAttendanceRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCommencementOfClassesRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "attendanceHandler")
@Component
public class AttendanceHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    SlaveAttendanceRepository slaveAttendanceRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    AttendanceTypeRepository attendanceTypeRepository;

    @Autowired
    CommencementOfClassesRepository commencementOfClassesRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_attendances_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String subjectUUID = serverRequest.queryParam("subjectUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !subjectUUID.isEmpty()) {
            Flux<SlaveAttendanceDto> slaveAttendanceFlux = slaveAttendanceRepository
                    .indexAttendanceWithStatusAndSubjectFilter(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAttendanceFlux
                    .collectList()
                    .flatMap(attendanceEntity -> slaveAttendanceRepository
                            .countAttendanceWithStatusFilterAgainstSubject(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (attendanceEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", attendanceEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!subjectUUID.isEmpty()) {
            Flux<SlaveAttendanceDto> slaveAttendanceFlux = slaveAttendanceRepository
                    .indexAttendanceAgainstSubjectWithoutStatusFilter(UUID.fromString(subjectUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAttendanceFlux
                    .collectList()
                    .flatMap(attendanceEntity -> slaveAttendanceRepository
                            .countAttendanceWithoutStatusFilterAgainstSubject(UUID.fromString(subjectUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (attendanceEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", attendanceEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }  else if (!status.isEmpty()) {
            Flux<SlaveAttendanceDto> slaveAttendanceFlux = slaveAttendanceRepository
                    .indexAttendanceWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveAttendanceFlux
                    .collectList()
                    .flatMap(attendanceEntity -> slaveAttendanceRepository
                            .countAttendanceWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (attendanceEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", attendanceEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveAttendanceDto> slaveAttendanceFlux = slaveAttendanceRepository
                    .indexAttendanceWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAttendanceFlux
                    .collectList()
                    .flatMap(attendanceEntity -> slaveAttendanceRepository
                            .countAttendanceWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (attendanceEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", attendanceEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_attendances_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID attendanceUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAttendanceRepository.findByUuidAndDeletedAtIsNull(attendanceUUID)
                .flatMap(attendanceEntity -> responseSuccessMsg("Record Fetched Successfully", attendanceEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_attendances_store")
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

                    UUID markedBy = null;
                    if ((value.containsKey("markedBy") && (!Objects.equals(value.getFirst("markedBy"), "")))) {
                        markedBy = UUID.fromString(value.getFirst("markedBy").trim());
                    }

                    AttendanceEntity entity = AttendanceEntity.builder()
                            .uuid(UUID.randomUUID())
                            .commencementOfClassesUUID(UUID.fromString(value.getFirst("commencementOfClassesUUID").trim()))
                            .attendanceTypeUUID(UUID.fromString(value.getFirst("attendanceTypeUUID").trim()))
                            .markedBy(markedBy)
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

                    //checks if commencement Of Classes uuid exists
                    return commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(entity.getCommencementOfClassesUUID())
                            //checks if attendance-type uuid exists
                            .flatMap(timetableEntity -> attendanceTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAttendanceTypeUUID())
                                    .flatMap(attendanceTypeEntity -> {
                                        if (entity.getMarkedBy() != null) {
                                            return teacherRepository.findByUuidAndDeletedAtIsNull(entity.getMarkedBy())
                                                    .flatMap(teacherEntity -> attendanceRepository.save(entity)
                                                            .flatMap(attendanceEntity -> responseSuccessMsg("Record Stored Successfully", attendanceEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."));
                                        } else {
                                            return attendanceRepository.save(entity)
                                                    .flatMap(attendanceEntity -> responseSuccessMsg("Record Stored Successfully", attendanceEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please contact developer."));
                                        }
                                    }).switchIfEmpty(responseInfoMsg("Attendance Type record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Attendance Type record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Commencement of Classes record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Commencement of Classes record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_attendances_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID attendanceUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        //Creating an Empty Status List to Add All the Boolean Values of Status
        List<Boolean> statusList = new ArrayList<>();

        return serverRequest.formData()
                .flatMap(value -> attendanceRepository.findByUuidAndDeletedAtIsNull(attendanceUUID)
                        .flatMap(previousEntity -> {

                            UUID markedBy = null;
                            if ((value.containsKey("markedBy") && (!Objects.equals(value.getFirst("markedBy"), "")))) {
                                markedBy = UUID.fromString(value.getFirst("markedBy").trim());
                            }

                            AttendanceEntity updatedAttendanceEntity = AttendanceEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .commencementOfClassesUUID(UUID.fromString(value.getFirst("commencementOfClassesUUID").trim()))
                                    .attendanceTypeUUID(UUID.fromString(value.getFirst("attendanceTypeUUID").trim()))
                                    .markedBy(markedBy)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
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

                            //checks if commencement Of Classes uuid exists
                            return commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(updatedAttendanceEntity.getCommencementOfClassesUUID())
                                    //checks if attendance-type uuid exists
                                    .flatMap(timetableEntity -> attendanceTypeRepository.findByUuidAndDeletedAtIsNull(updatedAttendanceEntity.getAttendanceTypeUUID())
                                            .flatMap(attendanceTypeEntity -> {
                                                if (updatedAttendanceEntity.getMarkedBy() != null) {
                                                    return teacherRepository.findByUuidAndDeletedAtIsNull(updatedAttendanceEntity.getMarkedBy())
                                                            .flatMap(teacherEntity -> attendanceRepository.save(previousEntity)
                                                                    .then(attendanceRepository.save(updatedAttendanceEntity))
                                                                    .flatMap(attendanceEntity -> responseSuccessMsg("Record Updated Successfully", attendanceEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."));
                                                } else {
                                                    return attendanceRepository.save(previousEntity)
                                                            .then(attendanceRepository.save(updatedAttendanceEntity))
                                                            .flatMap(attendanceEntity -> responseSuccessMsg("Record Updated Successfully", attendanceEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record.Please contact developer."));
                                                }
                                            }).switchIfEmpty(responseInfoMsg("Attendance Type record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Attendance Type record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Commencement of Classes record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Commencement of Classes record does not exist.Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_attendances_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID attendanceUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return attendanceRepository.findByUuidAndDeletedAtIsNull(attendanceUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AttendanceEntity entity = AttendanceEntity.builder()
                                        .uuid(val.getUuid())
                                        .attendanceTypeUUID(val.getAttendanceTypeUUID())
                                        .commencementOfClassesUUID(val.getCommencementOfClassesUUID())
                                        .markedBy(val.getMarkedBy())
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

                                return attendanceRepository.save(val)
                                        .then(attendanceRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_attendances_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID attendanceUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return attendanceRepository.findByUuidAndDeletedAtIsNull(attendanceUUID)
                .flatMap(attendanceEntity -> {

                    attendanceEntity.setDeletedBy(UUID.fromString(userId));
                    attendanceEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    attendanceEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    attendanceEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    attendanceEntity.setReqDeletedIP(reqIp);
                    attendanceEntity.setReqDeletedPort(reqPort);
                    attendanceEntity.setReqDeletedBrowser(reqBrowser);
                    attendanceEntity.setReqDeletedOS(reqOs);
                    attendanceEntity.setReqDeletedDevice(reqDevice);
                    attendanceEntity.setReqDeletedReferer(reqReferer);

                    return attendanceRepository.save(attendanceEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
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
