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
import tuf.webscaf.app.dbContext.master.entity.CourseSubjectEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseSubjectRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "courseSubjectHandler")
@Component
public class
CourseSubjectHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    SlaveCourseSubjectRepository slaveCourseSubjectRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    LectureTypeRepository lectureTypeRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SlaveSubjectRepository slaveSubjectRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_course-subjects_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Obe
        String obe = serverRequest.queryParam("obe").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Course UUID
        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //  status and course and obe are present in query params
        if (!status.isEmpty() && !courseUUID.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithCourseAndObeAndStatus(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(obe), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithCourseAndObeAndStatus(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(obe), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  course and obe are present in query params
        else if (!courseUUID.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithCourseAndObe(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(obe), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithCourseAndObe(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(obe))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // status and course are present in query params
        else if (!status.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithCourseAndStatus(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithCourseAndStatus(searchKeyWord, UUID.fromString(courseUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // status and obe are present in query params
        else if (!status.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithStatusAndObe(searchKeyWord, Boolean.valueOf(status), Boolean.valueOf(obe), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithStatusAndObe(searchKeyWord, Boolean.valueOf(status), Boolean.valueOf(obe))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        //  course is present in query params
        else if (!courseUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithCourse(searchKeyWord, UUID.fromString(courseUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithCourse(searchKeyWord, UUID.fromString(courseUUID))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // obe is present in query params
        else if (!obe.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithObe(searchKeyWord, Boolean.valueOf(obe), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithObe(searchKeyWord, Boolean.valueOf(obe))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // status is present in query params
        else if (!status.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if none of query params are present
        else {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository.countAllRecords(searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID courseSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCourseSubjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectUUID)
                .flatMap(courseSubjectEntity -> responseSuccessMsg("Record Fetched Successfully", courseSubjectEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    //    return course-subject against department
    @AuthHasPermission(value = "academic_api_v1_department_course-subjects_show")
    public Mono<ServerResponse> showCourseSubjectsAgainstDepartment(ServerRequest serverRequest) {

        String departmentUUID = serverRequest.queryParam("departmentUUID").map(String::toString).orElse("").trim();

        // Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Query Parameter Based of Obe
        String obe = serverRequest.queryParam("obe").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //  if status, department and obe is given
        if (!status.isEmpty() && !obe.isEmpty() && !departmentUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithDepartmentAndObeAndStatus(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(obe), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithDepartmentAndObeAndStatus(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(obe), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  if department and obe is given
        else if (!obe.isEmpty() && !departmentUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithDepartmentAndObe(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(obe), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithDepartmentAndObe(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(obe))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  if status is given
        else if (!status.isEmpty() && !departmentUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithDepartmentAndStatus(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithDepartmentAndStatus(searchKeyWord, UUID.fromString(departmentUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  if department is given
        else if (!departmentUUID.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithDepartment(searchKeyWord, UUID.fromString(departmentUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithDepartment(searchKeyWord, UUID.fromString(departmentUUID))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // obe is present in query params
        else if (!obe.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithObe(searchKeyWord, Boolean.valueOf(obe), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithObe(searchKeyWord, Boolean.valueOf(obe))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        //  if status is given
        else if (!status.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecords(searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    //    Show Course-Subject Against Academic Session And Teacher
    @AuthHasPermission(value = "academic_api_v1_course-subjects_academic-session_teacher_show")
    public Mono<ServerResponse> indexCourseSubjectAgainstSessionAndTeacher(ServerRequest serverRequest) {

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
            Flux<SlaveCourseSubjectDto> slaveSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexAgainstSessionAndTeacherWithStatusAndOpenLMS(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectAgainstSessionAndTeacherAndStatusAndOpenLMS(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), Boolean.valueOf(status))
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
            Flux<SlaveCourseSubjectDto> slaveSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexAgainstSessionAndTeacherWithStatus(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectAgainstSessionAndTeacherAndStatus(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(status))
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
            Flux<SlaveCourseSubjectDto> slaveSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexAgainstSessionAndTeacherWithOpenLMS(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectAgainstSessionAndTeacherAndOpenLMS(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), Boolean.valueOf(openLMS))
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
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectDtoFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexAgainstSessionAndTeacher(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseSubjectDtoFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectAgainstSessionAndTeacher(searchKeyWord, UUID.fromString(academicSessionUUID), UUID.fromString(teacherUUID))
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

    //    return course-subject against academic session
    @AuthHasPermission(value = "academic_api_v1_course-subjects_course-offered_subject-offered_show")
    public Mono<ServerResponse> showCourseSubjectsAgainstAcademicSession(ServerRequest serverRequest) {

        UUID academicSessionUUID = UUID.fromString(serverRequest.pathVariable("academicSessionUUID").trim());

        // Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithAcademicSessionAndStatus(searchKeyWord, academicSessionUUID, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithAcademicSessionAndStatus(searchKeyWord, academicSessionUUID, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexWithAcademicSession(searchKeyWord, academicSessionUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countAllRecordsWithAcademicSession(searchKeyWord, academicSessionUUID)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_course-offered_show")
    public Mono<ServerResponse> showCourseSubjectsOfOfferedCoursesAgainstAcademicSession(ServerRequest serverRequest) {

        UUID academicSessionUUID = UUID.fromString(serverRequest.pathVariable("academicSessionUUID").trim());

        // Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexOfOfferedCoursesWithAcademicSessionAndStatus(searchKeyWord, academicSessionUUID, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectIndexOfOfferedCoursesWithAcademicSessionAndStatus(searchKeyWord, academicSessionUUID, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCourseSubjectDto> slaveCourseSubjectFlux = slaveCourseSubjectRepository
                    .courseSubjectIndexOfOfferedCoursesWithAcademicSession(searchKeyWord, academicSessionUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseSubjectFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseSubjectRepository
                            .countCourseSubjectIndexOfOfferedCoursesWithAcademicSession(searchKeyWord, academicSessionUUID)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_un-mapped_show")
    public Mono<ServerResponse> showUnMappedSubjectsAgainstCourse(ServerRequest serverRequest) {

        final UUID courseUUID = UUID.fromString(serverRequest.pathVariable("courseUUID"));

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveSubjectEntity> slaveSubjectsFlux = slaveCourseSubjectRepository
                    .showUnmappedCourseSubjectListWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, courseUUID, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return courseRepository.findByUuidAndDeletedAtIsNull(courseUUID)
                    .flatMap(courseEntity -> slaveSubjectsFlux.collectList()
                            .flatMap(subjectEntity -> slaveSubjectRepository.countExistingCourseSubjectsRecordsWithStatus(courseUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                                    .flatMap(count -> {
                                        if (subjectEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Course Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Course Record does not exist. Please contact developer."));
        } else {
            Flux<SlaveSubjectEntity> slaveSubjectsFlux = slaveCourseSubjectRepository
                    .showUnmappedCourseSubjectList(searchKeyWord, searchKeyWord, searchKeyWord, courseUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return courseRepository.findByUuidAndDeletedAtIsNull(courseUUID)
                    .flatMap(courseEntity -> slaveSubjectsFlux.collectList()
                            .flatMap(subjectEntity -> slaveSubjectRepository.countExistingCourseSubjectsRecords(courseUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                                    .flatMap(count -> {
                                        if (subjectEntity.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);
                                        } else {
                                            return responseIndexSuccessMsg("All Records Fetched Successfully", subjectEntity, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."))
                    ).switchIfEmpty(responseInfoMsg("Course Record does not exist"))
                    .onErrorResume(ex -> responseErrorMsg("Course Record does not exist. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_mapped_show")
    public Mono<ServerResponse> showMappedSubjectsAgainstCourse(ServerRequest serverRequest) {

        final UUID courseUUID = UUID.fromString(serverRequest.pathVariable("courseUUID"));

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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveSubjectEntity> slaveSubjectsFlux = slaveCourseSubjectRepository
                    .showMappedCourseSubjectListWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, courseUUID, Boolean.valueOf(status),
                            directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectsFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository
                            .countMappedCourseSubjectsWithStatus(courseUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveSubjectEntity> slaveSubjectsFlux = slaveCourseSubjectRepository
                    .showMappedCourseSubjectList(searchKeyWord, searchKeyWord, searchKeyWord, courseUUID, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectsFlux
                    .collectList()
                    .flatMap(subjectEntity -> slaveSubjectRepository.countMappedCourseSubjects(courseUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", subjectEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_store")
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

                    CourseSubjectEntity entity = CourseSubjectEntity.builder()
                            .uuid(UUID.randomUUID())
                            .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
                            .courseUUID(UUID.fromString(value.getFirst("courseUUID").trim()))
                            .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                            .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                            .theoryCreditHours(value.getFirst("theoryCreditHours").trim())
                            .practicalCreditHours(value.getFirst("practicalCreditHours").trim())
                            .electiveSubject(Boolean.valueOf(value.getFirst("electiveSubject")))
                            .obe(Boolean.valueOf(value.getFirst("obe")))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .totalCreditHours(Integer.valueOf(value.getFirst("totalCreditHours")))
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

                    // check course uuid exists
                    return courseRepository.findByUuidAndDeletedAtIsNull(entity.getCourseUUID())
                            .flatMap(courseEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(entity.getLectureTypeUUID())
                                    // check subject uuid exists
                                    .flatMap(lectureTypeEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(entity.getSubjectUUID())
                                            // check semester uuid exists
                                            .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(entity.getSemesterUUID())
                                                    .flatMap(semesterEntity -> courseSubjectRepository
                                                            .findFirstByCourseUUIDAndSubjectUUIDAndDeletedAtIsNull(entity.getCourseUUID(), entity.getSubjectUUID())
                                                            .flatMap(courseSubject -> responseInfoMsg("Subject already exist"))
                                                            .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.save(entity)
                                                                    .flatMap(courseSubjectEntity -> responseSuccessMsg("Record Stored Successfully", courseSubjectEntity)
                                                                    ).switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))))
                                                    ).switchIfEmpty(responseInfoMsg("Semester record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Semester record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Subject does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Subject does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Lecture Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Lecture Type does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID courseSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectUUID)
                        .flatMap(previousCourseSubjectEntity -> {

                            CourseSubjectEntity updatedEntity = CourseSubjectEntity.builder()
                                    .uuid(previousCourseSubjectEntity.getUuid())
                                    .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                                    .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
                                    .courseUUID(UUID.fromString(value.getFirst("courseUUID").trim()))
                                    .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                                    .theoryCreditHours(value.getFirst("theoryCreditHours").trim())
                                    .practicalCreditHours(value.getFirst("practicalCreditHours").trim())
                                    .electiveSubject(Boolean.valueOf(value.getFirst("electiveSubject")))
                                    .obe(previousCourseSubjectEntity.getObe())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .totalCreditHours(Integer.valueOf(value.getFirst("totalCreditHours")))
                                    .createdAt(previousCourseSubjectEntity.getCreatedAt())
                                    .createdBy(previousCourseSubjectEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousCourseSubjectEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousCourseSubjectEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousCourseSubjectEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousCourseSubjectEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousCourseSubjectEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousCourseSubjectEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousCourseSubjectEntity.setDeletedBy(UUID.fromString(userId));
                            previousCourseSubjectEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousCourseSubjectEntity.setReqDeletedIP(reqIp);
                            previousCourseSubjectEntity.setReqDeletedPort(reqPort);
                            previousCourseSubjectEntity.setReqDeletedBrowser(reqBrowser);
                            previousCourseSubjectEntity.setReqDeletedOS(reqOs);
                            previousCourseSubjectEntity.setReqDeletedDevice(reqDevice);
                            previousCourseSubjectEntity.setReqDeletedReferer(reqReferer);

                            // check course uuid exists
                            return courseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseUUID())
                                    .flatMap(courseEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getLectureTypeUUID())
                                            // check subject uuid exists
                                            .flatMap(lectureTypeEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubjectUUID())
                                                    // check semester uuid exists
                                                    .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSemesterUUID())
                                                            .flatMap(semesterEntity -> courseSubjectRepository.findFirstByCourseUUIDAndSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCourseUUID(), updatedEntity.getSubjectUUID(), courseSubjectUUID)
                                                                    .flatMap(courseSubject -> responseInfoMsg("Subject already exist"))
                                                                    .switchIfEmpty(Mono.defer(() -> courseSubjectRepository.save(previousCourseSubjectEntity)
                                                                            .then(courseSubjectRepository.save(updatedEntity))
                                                                            .flatMap(courseSubjectEntity -> responseSuccessMsg("Record Updated Successfully", courseSubjectEntity))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))))
                                                            ).switchIfEmpty(responseInfoMsg("semester record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("semester record does not exist. Please contact developer"))
                                                    ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Lecture Type record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Lecture Type record does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Course record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course record does not exist. Please contact developer"));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID courseSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return courseSubjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CourseSubjectEntity courseSubjectEntity = CourseSubjectEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .semesterUUID(previousEntity.getSemesterUUID())
                                        .courseUUID(previousEntity.getCourseUUID())
                                        .subjectUUID(previousEntity.getSubjectUUID())
                                        .theoryCreditHours(previousEntity.getTheoryCreditHours())
                                        .practicalCreditHours(previousEntity.getPracticalCreditHours())
                                        .electiveSubject(previousEntity.getElectiveSubject())
                                        .obe(previousEntity.getObe())
                                        .lectureTypeUUID(previousEntity.getLectureTypeUUID())
                                        .totalCreditHours(previousEntity.getTotalCreditHours())
                                        .status(status == true ? true : false)
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return courseSubjectRepository.save(previousEntity)
                                        .then(courseSubjectRepository.save(courseSubjectEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-subjects_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID courseSubjectUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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

        return courseSubjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectUUID)
                .flatMap(courseSubjectEntity -> {

                    courseSubjectEntity.setDeletedBy(UUID.fromString(userId));
                    courseSubjectEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    courseSubjectEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    courseSubjectEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    courseSubjectEntity.setReqDeletedIP(reqIp);
                    courseSubjectEntity.setReqDeletedPort(reqPort);
                    courseSubjectEntity.setReqDeletedBrowser(reqBrowser);
                    courseSubjectEntity.setReqDeletedOS(reqOs);
                    courseSubjectEntity.setReqDeletedDevice(reqDevice);
                    courseSubjectEntity.setReqDeletedReferer(reqReferer);

                    return courseSubjectRepository.save(courseSubjectEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
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
