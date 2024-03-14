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
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentRegisteredCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "studentHandler")
@Component
public class StudentHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SlaveStudentRepository slaveStudentRepository;

    @Autowired
    SectionStudentPvtRepository sectionStudentPvtRepository;

    @Autowired
    StudentCourseRegistrationPvtRepository studentCourseRegistrationPvtRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    StudentDocumentRepository studentDocumentRepository;

    @Autowired
    StudentAcademicRecordRepository studentAcademicRecordRepository;

    @Autowired
    StudentJobHistoryRepository studentJobHistoryRepository;

    @Autowired
    StudentFinancialHistoryRepository studentFinancialHistoryRepository;

    @Autowired
    StudentAddressRepository studentAddressRepository;

    @Autowired
    StudentFamilyDoctorRepository studentFamilyDoctorRepository;

    @Autowired
    StudentAilmentPvtRepository studentAilmentPvtRepository;

    @Autowired
    StudentHobbyPvtRepository studentHobbyPvtRepository;

    @Autowired
    StudentNationalityPvtRepository studentNationalityPvtRepository;

    @Autowired
    StudentLanguagePvtRepository studentLanguagePvtRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CommencementOfClassesRepository commencementOfClassesRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentGroupStudentPvtRepository studentGroupStudentPvtRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    TeacherChildRepository teacherChildRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.ssl-status}")
    private String sslStatus;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialModuleUri;

    @AuthHasPermission(value = "academic_api_v1_students_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of courseOffered
        String courseOfferedUUID = serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse("").trim();

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

        if (!courseOfferedUUID.isEmpty() && !status.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveStudentRegisteredCourseDto> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCourseOfferedWithStatus(UUID.fromString(courseOfferedUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseOfferedWithStatus(UUID.fromString(courseOfferedUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!courseOfferedUUID.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveStudentRegisteredCourseDto> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCourseOffered(UUID.fromString(courseOfferedUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseOffered(UUID.fromString(courseOfferedUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .findAllByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .findAllByStudentIdContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    /** This Function is used By LMS Module in Assignment Attempt to check  Course Subject is same for both teacher subject**/
    public Mono<ServerResponse> indexStudentAgainstSessionAndCourseSubject(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of courseSubjectUUID
        String courseSubjectUUID = serverRequest.queryParam("courseSubjectUUID").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of teacher
        String teacherUUID = serverRequest.queryParam("teacherUUID").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of session
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

        if (!academicSessionUUID.isEmpty() && !status.isEmpty() && !courseSubjectUUID.isEmpty() && !teacherUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .findAllStudentsAgainstTeacherWithSameCourseSubjectAndSessionAndStatus(Boolean.valueOf(status), UUID.fromString(teacherUUID), UUID.fromString(courseSubjectUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countAllStudentsAgainstTeacherWithSameCourseSubjectAndSessionAndStatus(Boolean.valueOf(status), UUID.fromString(teacherUUID), UUID.fromString(courseSubjectUUID), UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .findAllStudentsAgainstTeacherWithSameCourseSubjectAndSession(UUID.fromString(teacherUUID), UUID.fromString(courseSubjectUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countAllStudentsAgainstTeacherWithSameCourseSubjectAndSession(UUID.fromString(teacherUUID), UUID.fromString(courseSubjectUUID), UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    /**
     * Fetch Students Based on Optional Campus , Course and Academic Session (This Function is used by Enrollments Handler)
     **/
    @AuthHasPermission(value = "academic_api_v1_academic-session_campus_course_students_index")
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

            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampusCourseAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCampusCourseAndAcademicSessionWithStatus(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if All three of academic Session , Campus ,course is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !courseUUID.isEmpty()) {

            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampusCourseAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCampusCourseAndAcademicSession(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampusAndAcademicSessionWithStatus(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCampusAndAcademicSessionWithStatus(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Course is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCoursesAndAcademicSessionWithStatus(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseAndAcademicSessionWithStatus(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (With Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCoursesAndCampusWithStatus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseAndCampusWithStatus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and Campus  is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampusAndAcademicSession(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCampusAndAcademicSession(UUID.fromString(academicSessionUUID), UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic Session and courses is present (Without Status)
        else if (!academicSessionUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCoursesAndAcademicSession(UUID.fromString(courseUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseAndAcademicSession(UUID.fromString(academicSessionUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus and courses is present (Without Status)
        else if (!campusUUID.isEmpty() && !courseUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCoursesAndCampus(UUID.fromString(courseUUID), UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countWithCourseAndCampus(UUID.fromString(campusUUID), UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present (With Status)
        else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithAcademicSessionsWithStatus(UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countAgainstSessionWithStatus(UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present (With Status)
        else if (!campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countCampusWithStatus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if courses is present (With Status)
        else if (!courseUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCoursesWithStatus(UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countCourseWithStatus(UUID.fromString(courseUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if academic session is present only
        else if (!academicSessionUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countAgainstSessionWithoutStatus(UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if campus is present only
        else if (!campusUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCampus(UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countCampusWithoutStatus(UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        // if course is present only
        else if (!courseUUID.isEmpty()) {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexWithCourses(UUID.fromString(courseUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countCourseWithoutStatus(UUID.fromString(courseUUID), searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"));
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexStudentsWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countStudentsWithStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
                    .indexStudentsWithoutStatus(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntity -> slaveStudentRepository.countStudentsWithoutStatus(searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

//    public Mono<ServerResponse> indexWithStudentExcluded(ServerRequest serverRequest) {
//
//        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
//
//        //Optional Query Parameter Based of Status
//        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();
//
//        // query param of student uuid
//        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("");
//
//        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
//        if (size > 100) {
//            size = 100;
//        }
//        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
//        int page = pageRequest - 1;
//
//        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
//        Sort.Direction direction;
//        switch (d.toLowerCase()) {
//            case "asc":
//                direction = Sort.Direction.ASC;
//                break;
//            case "desc":
//                direction = Sort.Direction.DESC;
//                break;
//            default:
//                direction = Sort.Direction.ASC;
//        }
//
//        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//        if (!status.isEmpty()) {
//            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
//                    .findAllByStudentIdContainingIgnoreCaseAndUuidIsNotAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentUUID), Boolean.valueOf(status));
//
//            return slaveStudentEntityFlux
//                    .collectList()
//                    .flatMap(studentEntity -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndUuidIsNotAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentUUID), Boolean.valueOf(status))
//                            .flatMap(count ->
//                            {
//                                if (studentEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        } else {
//            Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository
//                    .findAllByStudentIdContainingIgnoreCaseAndUuidIsNotAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentUUID));
//
//            return slaveStudentEntityFlux
//                    .collectList()
//                    .flatMap(studentEntity -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndUuidIsNotAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentUUID))
//                            .flatMap(count ->
//                            {
//                                if (studentEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully.", studentEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
//    }

    @AuthHasPermission(value = "academic_api_v1_students_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                .flatMap(academicSessionEntity -> responseSuccessMsg("Record Fetched Successfully.", academicSessionEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_students_check-student_show")
    public Mono<ServerResponse> checkStudentExistence(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentRepository.findFirstByUuidAndDeletedAtIsNull(studentUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseInfoMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist"));
    }

    //  Show  unmapped Students Against Financial Student Group
    public Mono<ServerResponse> showUnMappedStudentAgainstFinancialStudentGroup(ServerRequest serverRequest) {

        final UUID financialStudentGroupUUID = UUID.fromString(serverRequest.pathVariable("financialStudentGroupUUID"));
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();
        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Page index must not be less than zero");
        }
        if (size < 1) {
            return responseErrorMsg("Page size must not be less than one");
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
        return apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-group-student/list/show/", financialStudentGroupUUID)
                .flatMap(financialStdGroupJson -> {
                    List<UUID> listOfStdGroupUUID = new ArrayList<>();
                    try {
                        listOfStdGroupUUID = apiCallService.getUUIDList(financialStdGroupJson);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository.
                            findAllByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, Boolean.valueOf(status), listOfStdGroupUUID);
                    List<UUID> finalListOfStudent = listOfStdGroupUUID;
                    return slaveStudentEntityFlux
                            .collectList()
                            .flatMap(studentEntityDB -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(searchKeyWord, Boolean.valueOf(status), finalListOfStudent)
                                    .flatMap(count ->
                                    {
                                        if (studentEntityDB.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);

                                        } else {
                                            return responseIndexSuccessMsg("Records fetched successfully.", studentEntityDB, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read request"));
    }


    //    //Get Mapped Students Against Financial Student Group UUID
    @AuthHasPermission(value = "academic_api_v1_students_mapped_show")
    public Mono<ServerResponse> showMappedStudentAgainstFinancialStudentGroup(ServerRequest serverRequest) {

        final UUID financialStudentGroupUUID = UUID.fromString(serverRequest.pathVariable("financialStudentGroupUUID"));
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Page index must not be less than zero");
        }
        if (size < 1) {
            return responseErrorMsg("Page size must not be less than one");
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        return apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-group-student/list/show/", financialStudentGroupUUID)
                .flatMap(jsonNode -> {
                    List<UUID> listOfUUIDs = new ArrayList<>();
                    try {
                        listOfUUIDs = apiCallService.getUUIDList(jsonNode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Flux<SlaveStudentEntity> slaveStudentEntityFlux = slaveStudentRepository.
                            findAllByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(pageable, searchKeyWord, Boolean.valueOf(status), listOfUUIDs);

                    List<UUID> finalListOfIds = listOfUUIDs;
                    return slaveStudentEntityFlux
                            .collectList()
                            .flatMap(studentEntityDBList -> slaveStudentRepository.countByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(searchKeyWord, Boolean.valueOf(status), finalListOfIds)
                                    .flatMap(count ->
                                    {
                                        if (studentEntityDBList.isEmpty()) {
                                            return responseIndexInfoMsg("Record does not exist", count);

                                        } else {
                                            return responseIndexSuccessMsg("Records fetched successfully.", studentEntityDBList, count);
                                        }
                                    })
                            ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read request.Please Contact Developer."));
    }

    //This Function Is used By *Student Financial Module* to Check if Student UUID exists
    @AuthHasPermission(value = "academic_api_v1_students_list_show")
    public Mono<ServerResponse> showStudentListInFinancial(ServerRequest serverRequest) {
        return serverRequest.formData()
                .flatMap(value -> {
                    //This is List of Student that User gets from Front
                    List<String> listOfStudents = value.get("studentUUID");
                    //check if Key is StudentUUID and is not equals to Empty String
                    if (value.containsKey("studentUUID")) {
                        listOfStudents.removeIf(s -> s.equals(""));
                    }

                    //This is Final Student List to paas in the query
                    List<UUID> finalStudentList = new ArrayList<>();
                    if (value.get("studentUUID") != null) {
                        for (String students : listOfStudents) {
                            finalStudentList.add(UUID.fromString(students));
                        }
                    }

                    //Creating a Map so that UUID does not duplicate
                    Map<UUID, SlaveStudentDto> studentDtoMap = new HashMap<>();
                    return studentRepository.findAllByUuidInAndDeletedAtIsNull(finalStudentList)
                            .collectList()
                            .flatMap(stdEntity -> {

                                for (StudentEntity entity : stdEntity) {
                                    SlaveStudentDto stdDto = SlaveStudentDto.builder()
                                            .studentUUID(entity.getUuid())
                                            .build();
                                    studentDtoMap.put(entity.getUuid(), stdDto);
                                }
                                return responseSuccessMsg("Records Fetched Successfully", studentDtoMap);
                            });

                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_students_store")
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

                    StudentEntity studentEntity = StudentEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .studentId(value.getFirst("studentId"))
                            .officialEmail(value.getFirst("officialEmail").trim())
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

                    return studentRepository.findFirstByStudentIdAndDeletedAtIsNull(studentEntity.getStudentId())
                            .flatMap(courseEntity -> responseInfoMsg("Student Id already exist"))
                            .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(studentEntity.getCampusUUID())
                                    .flatMap(campusEntity -> studentRepository.save(studentEntity)
                                            .flatMap(stdEntityDB -> responseSuccessMsg("Record Stored Successfully.", stdEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Campus Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Campus Record does not exist. Please contact developer."))));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_students_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                        .flatMap(previousStdEntity -> {

                            StudentEntity updatedStudentEntity = StudentEntity.builder()
                                    .uuid(previousStdEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .studentId(value.getFirst("studentId"))
                                    .officialEmail(value.getFirst("officialEmail").trim())
                                    .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                                    .createdAt(previousStdEntity.getCreatedAt())
                                    .createdBy(previousStdEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousStdEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStdEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStdEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStdEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStdEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStdEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //Deleting Previous Student Entity
                            previousStdEntity.setDeletedBy(UUID.fromString(userId));
                            previousStdEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStdEntity.setReqDeletedIP(reqIp);
                            previousStdEntity.setReqDeletedPort(reqPort);
                            previousStdEntity.setReqDeletedBrowser(reqBrowser);
                            previousStdEntity.setReqDeletedOS(reqOs);
                            previousStdEntity.setReqDeletedDevice(reqDevice);
                            previousStdEntity.setReqDeletedReferer(reqReferer);

                            return studentRepository.findFirstByStudentIdAndDeletedAtIsNullAndUuidIsNot(updatedStudentEntity.getStudentId(), studentUUID)
                                    .flatMap(courseEntity -> responseInfoMsg("Student Id Already Exist"))
                                    .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(updatedStudentEntity.getCampusUUID())
                                            .flatMap(campusEntity -> studentRepository.save(previousStdEntity)
                                                    .then(studentRepository.save(updatedStudentEntity))
                                                    .flatMap(stdEntity -> responseSuccessMsg("Record Updated Successfully", stdEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Campus Record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Campus Record does not exist. Please contact developer."))));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer"))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_students_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                //Checks if Student Reference exists in Student Profiles
                .flatMap(studentEntity -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                .flatMap(studentProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                                //Checks if Student Reference exists in Student ContactNo
                                .switchIfEmpty(Mono.defer(() -> studentContactNoRepository.findFirstByStudentMetaUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Family Doctors
                                .switchIfEmpty(Mono.defer(() -> studentFamilyDoctorRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Documents
                                .switchIfEmpty(Mono.defer(() -> studentDocumentRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Job History
                                .switchIfEmpty(Mono.defer(() -> studentJobHistoryRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Financial History
                                .switchIfEmpty(Mono.defer(() -> studentFinancialHistoryRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Academic Record
                                .switchIfEmpty(Mono.defer(() -> studentAcademicRecordRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentAcademicRecordEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Addresses
                                .switchIfEmpty(Mono.defer(() -> studentAddressRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Ailment Pvt
                                .switchIfEmpty(Mono.defer(() -> studentAilmentPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Hobby Pvt
                                .switchIfEmpty(Mono.defer(() -> studentHobbyPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Nationality Pvt
                                .switchIfEmpty(Mono.defer(() -> studentNationalityPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Language Pvt
                                .switchIfEmpty(Mono.defer(() -> studentLanguagePvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentLanguagePvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Spouses
                                .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentSpouseEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Teacher Spouses
                                .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(teacherSpouseEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Mother
                                .switchIfEmpty(Mono.defer(() -> studentMotherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentMotherEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Father
                                .switchIfEmpty(Mono.defer(() -> studentFatherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentFatherEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Teacher Child
                                .switchIfEmpty(Mono.defer(() -> teacherChildRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(teacherChildEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Child
                                .switchIfEmpty(Mono.defer(() -> studentChildRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentChildEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Teacher Sibling
                                .switchIfEmpty(Mono.defer(() -> teacherSiblingRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(teacherSiblingEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Sibling
                                .switchIfEmpty(Mono.defer(() -> studentSiblingRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentSiblingEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Guardian
                                .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Registration
                                .switchIfEmpty(Mono.defer(() -> registrationRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(registrationEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Commencement of Classes
                                .switchIfEmpty(Mono.defer(() -> commencementOfClassesRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(registrationEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Enrollments
                                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(enrollmentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Section Pvt
                                .switchIfEmpty(Mono.defer(() -> sectionStudentPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(sectionStudentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student Course Registration
                                .switchIfEmpty(Mono.defer(() -> studentCourseRegistrationPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentCourseRegistrationPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Notifications
                                .switchIfEmpty(Mono.defer(() -> notificationRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(notificationEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                                //Checks if Student Reference exists in Student GroupStudent Pvt
                                .switchIfEmpty(Mono.defer(() -> studentGroupStudentPvtRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                        .flatMap(studentGroupStudentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
//                              check if Account reference exists in financial accounts in student financial module
                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(studentFinancialModuleUri + "api/v1/financial-student-accounts/student/show/", studentEntity.getUuid())
                                        .flatMap(jsonNode -> apiCallService.checkStatus(jsonNode)
                                                .flatMap(checkBranchUUIDApiMsg -> responseInfoMsg("Unable to delete Record.Reference of record exists!")))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    studentEntity.setDeletedBy(UUID.fromString(userId));
                                    studentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentEntity.setReqDeletedIP(reqIp);
                                    studentEntity.setReqDeletedPort(reqPort);
                                    studentEntity.setReqDeletedBrowser(reqBrowser);
                                    studentEntity.setReqDeletedOS(reqOs);
                                    studentEntity.setReqDeletedDevice(reqDevice);
                                    studentEntity.setReqDeletedReferer(reqReferer);

                                    return studentRepository.save(studentEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_students_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    Boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                            .flatMap(previousStdEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStdEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentEntity updatedStudentEntity = StudentEntity
                                        .builder()
                                        .uuid(previousStdEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .officialEmail(previousStdEntity.getOfficialEmail())
                                        .studentId(previousStdEntity.getStudentId())
                                        .campusUUID(previousStdEntity.getCampusUUID())
                                        .createdAt(previousStdEntity.getCreatedAt())
                                        .createdBy(previousStdEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStdEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStdEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStdEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStdEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStdEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStdEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousStdEntity.setDeletedBy(UUID.fromString(userId));
                                previousStdEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStdEntity.setReqDeletedIP(reqIp);
                                previousStdEntity.setReqDeletedPort(reqPort);
                                previousStdEntity.setReqDeletedBrowser(reqBrowser);
                                previousStdEntity.setReqDeletedOS(reqOs);
                                previousStdEntity.setReqDeletedDevice(reqDevice);
                                previousStdEntity.setReqDeletedReferer(reqReferer);

                                return studentRepository.save(previousStdEntity)
                                        .then(studentRepository.save(updatedStudentEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
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
