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
import tuf.webscaf.app.dbContext.master.entity.SubjectEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrolledSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.SlugifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "subjectHandler")
@Component
public class SubjectHandler {

    @Autowired
    CustomResponse appresponse;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    SlaveSubjectRepository slaveSubjectRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    CampusRepository campusRepository;
    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;
    @Autowired
    TimetableCreationRepository timetableCreationRepository;
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    TeacherOutlineRepository teacherOutlineRepository;
    @Autowired
    SlugifyHelper slugifyHelper;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subjects_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        String teacherUUID = serverRequest.queryParam("teacherUUID").map(String::toString).orElse("").trim();


        if (!status.isEmpty() && !academicSessionUUID.isEmpty() && !teacherUUID.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveSubjectEntity> slaveSubjectFlux = slaveSubjectRepository
                    .indexSubjectAgainstAcademicSessionWithStatusFilter(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstAcademicSessionWithStatusFilter(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty() && !academicSessionUUID.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveSubjectEntity> slaveSubjectFlux = slaveSubjectRepository
                    .indexSubjectAgainstAcademicSessionWithStatusFilter(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstAcademicSessionWithStatusFilter(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!academicSessionUUID.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveSubjectEntity> slaveSubjectFlux = slaveSubjectRepository
                    .indexSubjectAgainstAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

            Flux<SlaveSubjectEntity> slaveSubjectFlux = slaveSubjectRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveSubjectEntity> slaveSubjectFlux = slaveSubjectRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    //    Show Subject Against Academic Session And Teacher
    @AuthHasPermission(value = "academic_api_v1_academic-sessions_teachers_subjects_index")
    public Mono<ServerResponse> indexSubjectAgainstSessionAndTeacher(ServerRequest serverRequest) {

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String openLMS = serverRequest.queryParam("openLMS").map(String::toString).orElse("").trim();

        // Query Parameter of Academic Session UUID
        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        // Query Parameter of Academic Session UUID
        String teacherUUID = serverRequest.queryParam("teacherUUID").map(String::toString).orElse("").trim();

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !openLMS.isEmpty()) {
            Flux<SlaveSubjectDto> slaveSubjectFlux = slaveSubjectRepository
                    .fetchSubjectWithStatusAndOpenLMSFilter(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstAcademicSessionAndTeacherWithOpenLMSAndStatus(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSubjectDto> slaveSubjectFlux = slaveSubjectRepository
                    .showSubjectAgainstTeacherAndAcademicSessionWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstAcademicSessionAndTeacherWithStatus(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!openLMS.isEmpty()) {
            Flux<SlaveSubjectDto> slaveSubjectFlux = slaveSubjectRepository
                    .fetchSubjectWithOpenLMSFilter(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstAcademicSessionAndTeacherWithOpenLMS(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectDto> slaveSubjectFlux = slaveSubjectRepository
                    .showSubjectAgainstTeacherAndAcademicSession(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstAcademicSessionAndTeacher(UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    /**
     * This Function is used by LMS Module to fetch Subjects in which Students Are Enrolled With & Without Status Filter
     **/
    @AuthHasPermission(value = "academic_api_v1_enrolled-students_subjects_index")
    public Mono<ServerResponse> indexSubjectsAgainstEnrolledStudents(ServerRequest serverRequest) {

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Query Parameter of Student UUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

        // Query Parameter of Course UUID
        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !studentUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveEnrolledSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectAgainstCourseAndStudentWithStatusFilter(UUID.fromString(studentUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstCourseAndStudentWithStatus(UUID.fromString(studentUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveEnrolledSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectAgainstCourseAndStudentWithoutStatusFilter(UUID.fromString(studentUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstCourseAndStudentWithoutStatus(UUID.fromString(studentUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveEnrolledCourseSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectAgainstCourseWithStatusFilter(UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstCourseWithStatus(UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveEnrolledSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchEnrolledSubjectWithStatusFilter(UUID.fromString(studentUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstEnrolledStudentWithStatus(UUID.fromString(studentUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentUUID.isEmpty()) {
            Flux<SlaveEnrolledSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchEnrolledSubjectWithoutStatusFilter(UUID.fromString(studentUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstEnrolledStudentWithoutStatus(UUID.fromString(studentUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!courseUUID.isEmpty()) {
            Flux<SlaveEnrolledCourseSubjectDto> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectAgainstCourseWithoutStatusFilter(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectAgainstCourseWithoutStatus(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSubjectEntity> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectEntity> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_student_course_semester_index")
    public Mono<ServerResponse> indexSubjectsAgainstStudentCourseAndSemester(ServerRequest serverRequest) {

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Query Parameter of Student UUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

        // Query Parameter of Semester UUID
        String semesterUUID = serverRequest.queryParam("semesterUUID").map(String::toString).orElse("").trim();

        // Query Parameter of Course UUID
        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !semesterUUID.isEmpty() && !courseUUID.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveSubjectEntity> slaveSubjectEntityFlux = slaveSubjectRepository
                    .fetchSubjectAgainstStudentCourseAndSemesterWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, UUID.fromString(studentUUID), UUID.fromString(courseUUID), UUID.fromString(semesterUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectEntityFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstStudentCourseAndSemesterWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, UUID.fromString(studentUUID), UUID.fromString(courseUUID), UUID.fromString(semesterUUID))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectEntity> slaveEnrolledSubjectDtoFlux = slaveSubjectRepository
                    .fetchSubjectAgainstStudentCourseAndSemesterWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, UUID.fromString(studentUUID), UUID.fromString(courseUUID), UUID.fromString(semesterUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveEnrolledSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countSubjectsAgainstStudentCourseAndSemesterWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, UUID.fromString(studentUUID), UUID.fromString(courseUUID), UUID.fromString(semesterUUID))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectRepository.findByUuidAndDeletedAtIsNull(subjectUUID)
                .flatMap(subjectEntity -> responseSuccessMsg("Record Fetched Successfully", subjectEntity))
                .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_store")
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
                    SubjectEntity subjectEntityDB = SubjectEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .shortName(value.getFirst("shortName").trim())
                            .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                            .description(value.getFirst("description").trim())
                            .code(value.getFirst("code").trim())
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

                    // check name is unique
                    return subjectRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(subjectEntityDB.getName())
                            .flatMap(subjectEntity -> responseInfoMsg("Name Already Exist"))
                            // check Short Name is unique
                            .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstByShortNameIgnoreCaseAndDeletedAtIsNull(subjectEntityDB.getShortName())
                                    .flatMap(checkSlug -> responseInfoMsg("Short Name already Exists"))))
                            // check slug is unique
                            .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstBySlugAndDeletedAtIsNull(subjectEntityDB.getSlug())
                                    .flatMap(checkSlug -> responseInfoMsg("Slug already Exists"))))
                            // check subject code is unique
                            .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNull(subjectEntityDB.getCode())
                                    .flatMap(subjectEntity -> responseInfoMsg("Code Already Exist"))
                            )).switchIfEmpty(Mono.defer(() -> subjectRepository.save(subjectEntityDB)
                                    .flatMap(subjectEntity -> responseSuccessMsg("Record Stored Successfully", subjectEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectRepository.findByUuidAndDeletedAtIsNull(subjectUUID)
                        .flatMap(entity -> {

                            SubjectEntity updatedEntity = SubjectEntity.builder()
                                    .uuid(entity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .shortName(value.getFirst("shortName").trim())
                                    .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                                    .description(value.getFirst("description").trim())
                                    .code(value.getFirst("code").trim())
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

                            // check name is unique
                            return subjectRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), subjectUUID)
                                    .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                    // check short name is unique
                                    .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstByShortNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getShortName(), subjectUUID)
                                            .flatMap(checkSlug -> responseInfoMsg("The Entered Short Name already Exists"))))
                                    // check slug is unique
                                    .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getSlug(), subjectUUID)
                                            .flatMap(checkSlug -> responseInfoMsg("The Entered Slug already Exists"))))
                                    // check subject code is unique
                                    .switchIfEmpty(Mono.defer(() -> subjectRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(), subjectUUID)
                                            .flatMap(subjectEntity -> responseInfoMsg("Code Already Exist"))
                                    )).switchIfEmpty(Mono.defer(() -> subjectRepository.save(entity)
                                            .then(subjectRepository.save(updatedEntity))
                                            .flatMap(saveStatus -> responseSuccessMsg("Record Updated Successfully", updatedEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to Update Record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to Update Record. Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectRepository.findByUuidAndDeletedAtIsNull(subjectUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectEntity entity = SubjectEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .shortName(val.getShortName())
                                        .slug(val.getSlug())
                                        .description(val.getDescription())
                                        .code(val.getCode())
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

                                return subjectRepository.save(val)
                                        .then(subjectRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subjects_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectRepository.findByUuidAndDeletedAtIsNull(subjectUUID)
                .flatMap(subjectEntity -> {

                    subjectEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subjectEntity.setDeletedBy(UUID.fromString(userId));
                    subjectEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subjectEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subjectEntity.setReqDeletedIP(reqIp);
                    subjectEntity.setReqDeletedPort(reqPort);
                    subjectEntity.setReqDeletedBrowser(reqBrowser);
                    subjectEntity.setReqDeletedOS(reqOs);
                    subjectEntity.setReqDeletedDevice(reqDevice);
                    subjectEntity.setReqDeletedReferer(reqReferer);

                    return subjectRepository.save(subjectEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to deleted record")).onErrorResume(ex -> responseErrorMsg("Unable to deleted record"));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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



