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
import tuf.webscaf.app.dbContext.master.entity.SubjectOfferedEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectOfferedRepository;
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

@Tag(name = "subjectOfferedHandler")
@Component
public class

SubjectOfferedHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    SlaveSubjectOfferedRepository slaveSubjectOfferedRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SlaveSubjectRepository slaveSubjectRepository;

    @Autowired
    SubjectObeCloPvtRepository subjectObeCloPvtRepository;

    @Autowired
    SubjectFeeRepository subjectFeeRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subject-offered_index")
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

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String obe = serverRequest.queryParam("obe").map(String::toString).orElse("").trim();

        String courseUUID = serverRequest.queryParam("courseUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if status, course and obe are given
        if (!status.isEmpty() && !courseUUID.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstCourseWithStatusAndOBE(Boolean.valueOf(status), Boolean.valueOf(obe), UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countSubjectOfferedAgainstCourseOBEAndStatus(UUID.fromString(courseUUID), Boolean.valueOf(obe), Boolean.valueOf(status), searchKeyWord)
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
        // if status and course are given
        else if (!status.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstCourseWithStatus(Boolean.valueOf(status), UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countRecordsWithSubjectOfferedAgainstCourseAndStatus(UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
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
        // if status and obe are given
        else if (!status.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexWithStatusAndOBE(Boolean.valueOf(status), Boolean.valueOf(obe), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countSubjectOfferedAgainstOBEAndStatus(Boolean.valueOf(obe), Boolean.valueOf(status), searchKeyWord)
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
        // if course and obe are given
        else if (!courseUUID.isEmpty() && !obe.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstCourseAndOBE(Boolean.valueOf(obe), UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countSubjectOfferedAgainstCourseAndOBE(UUID.fromString(courseUUID), Boolean.valueOf(obe), searchKeyWord)
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
        // if obe is given
        else if (!obe.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexWithOBE(Boolean.valueOf(obe), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countSubjectOfferedWithObeFilter(Boolean.valueOf(obe), searchKeyWord)
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
        // course is given
        else if (!courseUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstCourse(UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countRecordsWithSubjectOfferedAgainstCourse(UUID.fromString(courseUUID), searchKeyWord)
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

        // if status is given
        else if (!status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countAllByDeletedAtIsNullAndStatus(searchKeyWord, Boolean.valueOf(status))
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

        // none of query params are given
        else {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(subjectOfferedEntity -> slaveSubjectOfferedRepository.countAllByDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (subjectOfferedEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOfferedEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    /**
     * Fetch Subject Offered Based on Optional Campus , Course and Academic Session (This Function is used by Enrollments Handler)
     **/
    @AuthHasPermission(value = "academic_api_v1_academic-session_campus_courses_subject-offered_index")
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

            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampusCourseAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCampusAndCourseWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if All three of academic Session , Campus ,course is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !courseUUID.isEmpty()) {

            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampusCourseAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCampusAndCourseWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampusAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCampusWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), Boolean.valueOf(status), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Course is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCoursesAndAcademicSessionWithStatus(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCourseWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), Boolean.valueOf(status), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (With Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCoursesAndCampusWithStatus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countWithCourseAndCampusWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampusAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCampusWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and courses is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCoursesAndAcademicSession(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countRecordsAgainstAcademicSessionCourseWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (Without Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCoursesAndCampus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countWithCourseAndCampus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithAcademicSessionsWithStatus(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countAgainstSessionWithStatus(UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present (With Status)
        else if (!campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if courses is present (With Status)
        else if (!courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCoursesWithStatus(UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countCourseWithStatus(UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present only
        else if (!academicSessionUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countAgainstSessionWithoutStatus(UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present only
        else if (!campusUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCampus(UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countCampusWithoutStatus(UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if course is present only
        else if (!courseUUID.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .indexWithCourses(UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countCourseWithoutStatus(UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countAllByDeletedAtIsNullAndStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectOfferedDto> slaveCourseEntityFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCourseEntityFlux
                    .collectList()
                    .flatMap(subjectOfferedEntityDB -> slaveSubjectOfferedRepository.countAllByDeletedAtIsNull(searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectOfferedEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", subjectOfferedEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    /**
     * Subject Offered Against Student And Course With and WithOut Status
     **/
    @AuthHasPermission(value = "academic_api_v1_student_course_subject-offered_show")
    public Mono<ServerResponse> showSubjectOfferedAgainstCourseAndStudent(ServerRequest serverRequest) {

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

        Optional<String> status = serverRequest.queryParam("status");

        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));
        UUID courseUUID = UUID.fromString(serverRequest.queryParam("courseUUID").map(String::toString).orElse(""));

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (status.isPresent()) {
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstStudentAndCourseWithStatus
                            (Boolean.valueOf(status.get()), studentUUID, courseUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveSubjectOfferedRepository
                            .countRecordsWithSubjectOfferedStudentCourseAndStatus(studentUUID, courseUUID, searchKeyWord, Boolean.valueOf(status.get()))
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
            Flux<SlaveSubjectOfferedDto> slaveSubjectOfferedFlux = slaveSubjectOfferedRepository
                    .subjectOfferedIndexAgainstStudentAndCourse(studentUUID, courseUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveSubjectOfferedFlux
                    .collectList()
                    .flatMap(subjectOfferedEntity -> slaveSubjectOfferedRepository.countRecordsWithSubjectOfferedStudentCourse(studentUUID, courseUUID, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectOfferedEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOfferedEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-offered_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID SubjectOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectOfferedRepository.showByUuidAndDeletedAtIsNull(SubjectOfferedUUID)
                .flatMap(subjectOfferedEntity -> responseSuccessMsg("Record Fetched Successfully", subjectOfferedEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-offered_store")
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

                    SubjectOfferedEntity entity = SubjectOfferedEntity.builder()
                            .uuid(UUID.randomUUID())
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
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

//                    check course uuid exists
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicSessionUUID())
//                            check subject uuid exists
                            .flatMap(academicSessionEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(entity.getCourseSubjectUUID())
                                    .flatMap(courseSubjectEntity -> subjectOfferedRepository
                                            .findFirstByAcademicSessionUUIDAndCourseSubjectUUIDAndDeletedAtIsNull(entity.getAcademicSessionUUID(), entity.getCourseSubjectUUID())
                                            .flatMap(academicSessionAndCampusCourse -> responseInfoMsg("Subject already Offered in this Academic Session"))
                                            .switchIfEmpty(Mono.defer(() -> subjectOfferedRepository.save(entity)
                                                    .flatMap(subjectOfferedEntity -> responseSuccessMsg("Record Stored Successfully", subjectOfferedEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))))
                                    ).switchIfEmpty(responseInfoMsg("Course Subject does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course Subject does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-offered_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOfferedUUID)
                                .flatMap(previousSubjectOfferedEntity -> {

                                    SubjectOfferedEntity updatedEntity = SubjectOfferedEntity
                                            .builder()
                                            .uuid(previousSubjectOfferedEntity.getUuid())
                                            .courseSubjectUUID(UUID.fromString(value.getFirst("courseSubjectUUID").trim()))
                                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .createdAt(previousSubjectOfferedEntity.getCreatedAt())
                                            .createdBy(previousSubjectOfferedEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousSubjectOfferedEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousSubjectOfferedEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousSubjectOfferedEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousSubjectOfferedEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousSubjectOfferedEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousSubjectOfferedEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousSubjectOfferedEntity.setDeletedBy(UUID.fromString(userId));
                                    previousSubjectOfferedEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousSubjectOfferedEntity.setReqDeletedIP(reqIp);
                                    previousSubjectOfferedEntity.setReqDeletedPort(reqPort);
                                    previousSubjectOfferedEntity.setReqDeletedBrowser(reqBrowser);
                                    previousSubjectOfferedEntity.setReqDeletedOS(reqOs);
                                    previousSubjectOfferedEntity.setReqDeletedDevice(reqDevice);
                                    previousSubjectOfferedEntity.setReqDeletedReferer(reqReferer);

//                            check course uuid exists
                                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID())
//                            check subject uuid exists
                                            .flatMap(academicSessionEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseSubjectUUID())
                                                    .flatMap(courseSubjectEntity -> subjectOfferedRepository
                                                            .findFirstByAcademicSessionUUIDAndCourseSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getAcademicSessionUUID(), updatedEntity.getCourseSubjectUUID(), subjectOfferedUUID)
                                                            .flatMap(academicSessionAndCampusCourse -> responseInfoMsg("Subject already Offered in this Academic Session"))
                                                            .switchIfEmpty(Mono.defer(() -> subjectOfferedRepository.save(previousSubjectOfferedEntity)
                                                                    .then(subjectOfferedRepository.save(updatedEntity))
                                                                    .flatMap(subjectOfferedEntity -> responseSuccessMsg("Record Updated Successfully", subjectOfferedEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))))
                                                    ).switchIfEmpty(responseInfoMsg("Course Subject does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Course Subject does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer"));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-offered_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOfferedUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectOfferedEntity subjectOfferedEntity = SubjectOfferedEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .academicSessionUUID(previousEntity.getAcademicSessionUUID())
                                        .courseSubjectUUID(previousEntity.getCourseSubjectUUID())
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

                                return subjectOfferedRepository.save(previousEntity)
                                        .then(subjectOfferedRepository.save(subjectOfferedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-offered_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOfferedUUID)
                //check If subject offered UUID Exists in subject_offered_clo_pvt
                .flatMap(subjectOfferedEntity -> subjectFeeRepository.findFirstBySubjectOfferedUUIDAndDeletedAtIsNull(subjectOfferedEntity.getUuid())
                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists"))
                        //check If subject offered UUID Exists in enrollment
                        .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findFirstBySubjectOfferedUUIDAndDeletedAtIsNull(subjectOfferedEntity.getUuid())
                                .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            subjectOfferedEntity.setDeletedBy(UUID.fromString(userId));
                            subjectOfferedEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            subjectOfferedEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            subjectOfferedEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            subjectOfferedEntity.setReqDeletedIP(reqIp);
                            subjectOfferedEntity.setReqDeletedPort(reqPort);
                            subjectOfferedEntity.setReqDeletedBrowser(reqBrowser);
                            subjectOfferedEntity.setReqDeletedOS(reqOs);
                            subjectOfferedEntity.setReqDeletedDevice(reqDevice);
                            subjectOfferedEntity.setReqDeletedReferer(reqReferer);

                            return subjectOfferedRepository.save(subjectOfferedEntity)
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
