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
import tuf.webscaf.app.dbContext.master.entity.CourseEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.SlugifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "courseHandler")
@Component
public class CourseHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SlaveCourseRepository slaveCourseRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    FeeStructureRepository feeStructureRepository;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    NotificationDetailRepository notificationDetailRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    CourseLevelRepository courseLevelRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    CampusCourseOfferedPvtRepository campusCourseOfferedPvtRepository;

    @Autowired
    TeacherOutlineRepository teacherOutlineRepository;

    @Autowired
    CourseVisionAndMissionRepository courseVisionAndMissionRepository;

    @Autowired
    SlugifyHelper slugifyHelper;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_courses_index")
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of campusUUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();


        if (!status.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseFlux = slaveCourseRepository
                    .indexWithStudentAndStatus(UUID.fromString(studentUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsWithStudentAndStatusFilter(UUID.fromString(studentUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        } else if (!studentUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseFlux = slaveCourseRepository
                    .indexWithStudent(UUID.fromString(studentUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsWithStudentFilter(UUID.fromString(studentUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseFlux = slaveCourseRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        } else {
            Flux<SlaveCourseDto> slaveCourseFlux = slaveCourseRepository
                    .index(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecords(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }
    }

    /**
     * Fetch Courses Based on Optional Campus , Course and Academic Session (This Function is used by Enrollments Handler)
     **/

    @AuthHasPermission(value = "academic_api_v1_academic-session_campus_courses_index")
    public Mono<ServerResponse> indexWithCampusCourseAndSession(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of campusUUID
        String campusUUID = serverRequest.queryParam("campusUUID").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of courseUUID
        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of academicSessionUUID
        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

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

        // if All three of academic Session , Campus ,course and Status is present
        if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {

            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampusCourseAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCampusAndCourseWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if All three of academic Session , Campus ,course is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !courseUUID.isEmpty()) {

            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampusCourseAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCampusAndCourseWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampusAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCampusWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Course is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCoursesAndAcademicSessionWithStatus(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCourseWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (With Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCoursesAndCampusWithStatus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countWithCourseAndCampusWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampusAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCampusWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and courses is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCoursesAndAcademicSession(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsAgainstAcademicSessionCourseWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (Without Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCoursesAndCampus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countWithCourseAndCampus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithAcademicSessionsWithStatus(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countAgainstSessionWithStatus(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present (With Status)
        else if (!campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if courses is present (With Status)
        else if (!courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCoursesWithStatus(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countCourseWithStatus(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present only
        else if (!academicSessionUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countAgainstSessionWithoutStatus(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present only
        else if (!campusUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCampus(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countCampusWithoutStatus(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if course is present only
        else if (!courseUUID.isEmpty()) {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithCourses(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countCourseWithoutStatus(UUID.fromString(courseUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCourseDto> slaveCourseEntityFlux = slaveCourseRepository
                    .index(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(courseEntity -> slaveCourseRepository.countRecords(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (courseEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", courseEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_courses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID courseUUId = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCourseRepository.showByUUID(courseUUId)
                .flatMap(courseEntity -> responseSuccessMsg("Record Fetched Successfully", courseEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_courses_store")
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
                    Integer semesterNo = null;
                    if ((value.containsKey("noOfSemester") && (!value.getFirst("noOfSemester").isEmpty()))) {
                        semesterNo = Integer.valueOf(value.getFirst("noOfSemester"));

                    }

                    Integer annualNo = null;
                    if ((value.containsKey("noOfAnnuals") && (!value.getFirst("noOfAnnuals").isEmpty()))) {
                        annualNo = Integer.valueOf(value.getFirst("noOfAnnuals"));

                    }

                    CourseEntity entity = CourseEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                            .code(value.getFirst("code").trim())
                            .shortName(value.getFirst("shortName").trim().toUpperCase())
                            .eligibilityCriteria(value.getFirst("eligibilityCriteria").trim())
                            .courseLevelUUID(UUID.fromString(value.getFirst("courseLevelUUID")))
                            .duration(value.getFirst("duration").trim())
                            .isSemester(Boolean.parseBoolean(value.getFirst("isSemester")))
                            .departmentUUID(UUID.fromString(value.getFirst("departmentUUID")))
                            .minimumAgeLimit(Integer.valueOf(value.getFirst("minimumAgeLimit")))
                            .maximumAgeLimit(Integer.valueOf(value.getFirst("maximumAgeLimit")))
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

                    // if maximum age limit is less than minimum age limit
                    if (entity.getMaximumAgeLimit() <= entity.getMinimumAgeLimit()) {
                        return responseInfoMsg("Minimum Age Limit should not be greater or equal to Maximum Age Limit");
                    }

                    // if isSemester is true
                    if (entity.getIsSemester()) {
                        //if user selects is semester and Add No. of Annuals than message will display
                        if (annualNo != null) {
                            return responseInfoMsg("No.Of Annuals is not allowed with Semester Based Course");
                        } else if (semesterNo == null) {
                            return responseInfoMsg("Please Enter No.Of Semesters");
                        } else {
                            entity.setNoOfSemester(semesterNo);
                        }
                    } else {
                        //if IS Semester is Set to False then user will try to Enter No of Semesters than
                        if (semesterNo != null) {
                            return responseInfoMsg("Semester No is not allowed with Annual Based Course");
                        } else if (annualNo == null) {
                            return responseInfoMsg("Please Enter No.Of Annuals");
                        } else {
                            entity.setNoOfAnnuals(annualNo);
                        }
                    }

                    // check if name is unique
                    return courseRepository.findFirstByNameIgnoreCaseAndCourseLevelUUIDAndDeletedAtIsNull(entity.getName(), entity.getCourseLevelUUID())
                            .flatMap(courseEntity -> responseInfoMsg("Name Already Exist"))
                            // check if slug is unique
                            .switchIfEmpty(Mono.defer(() -> courseRepository.findFirstBySlugAndCourseLevelUUIDAndDeletedAtIsNull(entity.getSlug(),entity.getCourseLevelUUID())
                                    .flatMap(checkSlug -> responseInfoMsg("The Entered Slug already Exists"))))
                            //checks if department uuid exists
                            .switchIfEmpty(Mono.defer(() -> departmentRepository.findByUuidAndDeletedAtIsNull(entity.getDepartmentUUID())
                                    //checks if course level uuid exists
                                    .flatMap(departmentEntity -> courseLevelRepository.findByUuidAndDeletedAtIsNull(entity.getCourseLevelUUID())
                                            .flatMap(courseLevelEntity -> courseRepository.save(entity)
                                                    .flatMap(courseEntity -> responseSuccessMsg("Record Stored Successfully", courseEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Course Level does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Course Level does not exist.Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Department does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Department does not exist.Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_courses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID courseUUId = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> courseRepository.findByUuidAndDeletedAtIsNull(courseUUId)
                        .flatMap(previousEntity -> {

                            Integer semesterNo = null;
                            if ((value.containsKey("noOfSemester") && (!value.getFirst("noOfSemester").isEmpty()))) {
                                semesterNo = Integer.valueOf(value.getFirst("noOfSemester"));

                            }

                            Integer annualNo = null;
                            if ((value.containsKey("noOfAnnuals") && (!value.getFirst("noOfAnnuals").isEmpty()))) {
                                annualNo = Integer.valueOf(value.getFirst("noOfAnnuals"));

                            }

                            CourseEntity updatedEntity = CourseEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                                    .code(value.getFirst("code").trim())
                                    .shortName(value.getFirst("shortName").trim().toUpperCase())
                                    .eligibilityCriteria(value.getFirst("eligibilityCriteria").trim())
                                    .courseLevelUUID(UUID.fromString(value.getFirst("courseLevelUUID")))
                                    .duration(value.getFirst("duration").trim())
                                    .isSemester(Boolean.parseBoolean(value.getFirst("isSemester")))
                                    .departmentUUID(UUID.fromString(value.getFirst("departmentUUID")))
                                    .minimumAgeLimit(Integer.valueOf(value.getFirst("minimumAgeLimit")))
                                    .maximumAgeLimit(Integer.valueOf(value.getFirst("maximumAgeLimit")))
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

                            // if maximum age limit is less than minimum age limit
                            if (updatedEntity.getMaximumAgeLimit() <= updatedEntity.getMinimumAgeLimit()) {
                                return responseInfoMsg("Minimum Age Limit should not be greater or equal to Maximum Age Limit");
                            }

                            // if isSemester is true
                            if (updatedEntity.getIsSemester()) {
                                //if user selects is semester and Add No. of Annuals than message will display
                                if (annualNo != null) {
                                    return responseInfoMsg("No.Of Annuals is not allowed with Semester Based Course");
                                } else if (semesterNo == null) {
                                    return responseInfoMsg("Please Enter No.Of Semesters");
                                } else {
                                    updatedEntity.setNoOfSemester(semesterNo);
                                }
                            } else {
                                //if IS Semester is Set to False then user will try to Enter No of Semesters than
                                if (semesterNo != null) {
                                    return responseInfoMsg("Semester No is not allowed with Annual Based Course");
                                } else if (annualNo == null) {
                                    return responseInfoMsg("Please Enter No.Of Annuals");
                                } else {
                                    updatedEntity.setNoOfAnnuals(annualNo);
                                }
                            }

                            // check if name is unique
                            return courseRepository.findFirstByNameIgnoreCaseAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), updatedEntity.getCourseLevelUUID(), courseUUId)
                                    .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                    // check if slug is unique
                                    .switchIfEmpty(Mono.defer(() -> courseRepository.findFirstBySlugAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getSlug(),updatedEntity.getCourseLevelUUID() ,courseUUId)
                                            .flatMap(checkSlug -> responseInfoMsg("The Entered Slug already Exists"))))
                                    //checks if department uuid exists
                                    .switchIfEmpty(Mono.defer(() -> departmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDepartmentUUID())
                                            //checks if course level uuid exists
                                            .flatMap(departmentEntity -> courseLevelRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseLevelUUID())
                                                    .flatMap(courseLevel -> courseRepository.save(previousEntity)
                                                            .then(courseRepository.save(updatedEntity))
                                                            .flatMap(courseEntity -> responseSuccessMsg("Record Updated Successfully", courseEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record.Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Course Level does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Course Level does not exist.Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Department does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Department does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_courses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID courseUUId = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return courseRepository.findByUuidAndDeletedAtIsNull(courseUUId)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CourseEntity updatedEntity = CourseEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .code(previousEntity.getCode())
                                        .name(previousEntity.getName())
                                        .slug(previousEntity.getSlug())
                                        .description(previousEntity.getDescription())
                                        .shortName(previousEntity.getShortName())
                                        .courseLevelUUID(previousEntity.getCourseLevelUUID())
                                        .duration(previousEntity.getDuration())
                                        .isSemester(previousEntity.getIsSemester())
                                        .noOfSemester(previousEntity.getNoOfSemester())
                                        .noOfAnnuals(previousEntity.getNoOfAnnuals())
                                        .departmentUUID(previousEntity.getDepartmentUUID())
                                        .minimumAgeLimit(previousEntity.getMinimumAgeLimit())
                                        .maximumAgeLimit(previousEntity.getMaximumAgeLimit())
                                        .eligibilityCriteria(previousEntity.getEligibilityCriteria())
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

                                return courseRepository.save(previousEntity)
                                        .then(courseRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_courses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID courseUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return courseRepository.findByUuidAndDeletedAtIsNull(courseUUID)
                .flatMap(courseEntity -> {

                    courseEntity.setDeletedBy(UUID.fromString(userId));
                    courseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    courseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    courseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    courseEntity.setReqDeletedIP(reqIp);
                    courseEntity.setReqDeletedPort(reqPort);
                    courseEntity.setReqDeletedBrowser(reqBrowser);
                    courseEntity.setReqDeletedOS(reqOs);
                    courseEntity.setReqDeletedDevice(reqDevice);
                    courseEntity.setReqDeletedReferer(reqReferer);

                    return courseRepository.save(courseEntity)
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
