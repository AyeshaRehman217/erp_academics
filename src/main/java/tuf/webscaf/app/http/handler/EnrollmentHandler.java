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
import tuf.webscaf.app.dbContext.master.entity.AcademicSessionEntity;
import tuf.webscaf.app.dbContext.master.entity.EnrollmentEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrollmentDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveEnrollmentRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "enrollmentHandler")
@Component
public class EnrollmentHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SlaveEnrollmentRepository slaveEnrollmentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    CampusCourseRepository campusCourseRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Value("${server.zone}")
    private String zone;


    @AuthHasPermission(value = "academic_api_v1_enrollments_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Academic Session Query Parameter
        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        // Subject Query Parameter
        String subjectUUID = serverRequest.queryParam("subjectUUID").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //Academic Session && Subject && Status is present
        if (!academicSessionUUID.isEmpty() && !subjectUUID.isEmpty() && !status.isEmpty()) {
            return indexAgainstSessionAndSubjectWithStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        //Academic Session && Subject is present
        else if (!academicSessionUUID.isEmpty() && !subjectUUID.isEmpty()) {
            return indexAgainstSessionAndSubjectWithoutStatusFilter(UUID.fromString(academicSessionUUID), UUID.fromString(subjectUUID), searchKeyWord, directionProperty, d, pageable);
        }
        //Fetch Enrollments where Subject + Status is Present
        else if (!subjectUUID.isEmpty() && !status.isEmpty()) {
            return indexAgainstSubjectWithStatusFilter(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }

        //Fetch Enrollments where Academic Session + Status is Present
        else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            return indexAgainstSessionWithStatusFilter(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        //Fetch Enrollments where Academic Session is Present
        else if (!academicSessionUUID.isEmpty()) {
            return indexAgainstAcademicSession(UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable);
        }
        //Fetch Enrollments where Subject is Present
        else if (!subjectUUID.isEmpty()) {
            return indexAgainstSubjectWithoutStatusFilter(UUID.fromString(subjectUUID), searchKeyWord, directionProperty, d, pageable);
        }
        //Fetch Enrollments where Status is Present
        else if (!status.isEmpty()) {
            Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                    .indexAllRecordsWithStatusFilter(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveEnrollmentFlux
                    .collectList()
                    .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (enrollmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                    .indexAllRecordsWithOutStatusFilter(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveEnrollmentFlux
                    .collectList()
                    .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllByDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (enrollmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_enrollments_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID enrollmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveEnrollmentRepository.showRecordByUUID(enrollmentUUID)
                .flatMap(enrollmentDto -> responseSuccessMsg("Record Fetched Successfully", enrollmentDto))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_enrollments_store")
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

                    EnrollmentEntity enrollmentEntity1 = EnrollmentEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                            .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                            .courseUUID(UUID.fromString(value.getFirst("courseUUID").trim()))
                            .extra(Boolean.valueOf(value.getFirst("extra")))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .isOpenLMS(Boolean.valueOf(value.getFirst("isOpenLMS")))
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

//                    check student uuid exists
                    return studentRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity1.getStudentUUID())
//                            check semester exists
                            .flatMap(studentEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity1.getSemesterUUID())
//                                    check subject offered exists
                                            .flatMap(semesterEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity1.getSubjectOfferedUUID())
                                                    //check if campus exist in campuses
                                                    .flatMap(subjectOfferedEntity -> campusRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity1.getCampusUUID())
                                                            //check if the given academic session and subject offered exists in Subject Offered Table
                                                            .flatMap(campusEntity -> subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(enrollmentEntity1.getAcademicSessionUUID(), enrollmentEntity1.getSubjectOfferedUUID())
                                                                    //fetch the campus Course Record from CampusCourse Handler
                                                                    .flatMap(subjectOfferedEntity1 -> campusCourseRepository.findByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(enrollmentEntity1.getCampusUUID(), enrollmentEntity1.getCourseUUID())
                                                                            //check if the student is registered in the entered Campus Course
                                                                            .flatMap(campusCourse -> registrationRepository.findByStudentUUIDAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNull(enrollmentEntity1.getStudentUUID(), campusCourse.getUuid(), enrollmentEntity1.getAcademicSessionUUID())
                                                                                    .flatMap(registrationEntity -> {
                                                                                        //check if extra is checked then store enrollment entity
                                                                                        if (enrollmentEntity1.getExtra()) {
                                                                                            return enrollmentRepository.save(enrollmentEntity1)
                                                                                                    .flatMap(enrollmentEntity -> responseSuccessMsg("Record Stored Successfully", enrollmentEntity))
                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                                                        } else {
                                                                                            //check if the student is already exist against the academic session and semester
                                                                                            return enrollmentRepository.findFirstByStudentUUIDAndSubjectOfferedUUIDAndAcademicSessionUUIDAndSemesterUUIDAndDeletedAtIsNull(enrollmentEntity1.getStudentUUID(), enrollmentEntity1.getSubjectOfferedUUID(), enrollmentEntity1.getAcademicSessionUUID(), enrollmentEntity1.getSemesterUUID())
                                                                                                    .flatMap(checkMsg -> responseInfoMsg("The Student is Already Enrolled against the given semester and Subject Offered"))
                                                                                                    .switchIfEmpty(Mono.defer(() -> enrollmentRepository.save(enrollmentEntity1)
                                                                                                            .flatMap(enrollmentEntity -> responseSuccessMsg("Record Stored Successfully", enrollmentEntity))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                                                    ));
                                                                                        }
                                                                                    }).switchIfEmpty(responseInfoMsg("The Entered Student Is Not Registered Against this campus Course"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("The Entered Student Is Not Registered Against this campus Course.Please Contact Developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("The Entered Campus And Course Does not Exist in Campus Course"))
                                                                            .onErrorResume(ex -> responseErrorMsg("The Entered Campus And Course Does not Exist in Campus Course.Please Contact Developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Subject Offered Against the given Academic Session Does not Exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered Against the given Academic Session Does not Exist.Please Contact Developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Campus does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Campus does not exist.Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Subject Offered does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Semester does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Semester does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Student does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollments_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID enrollmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> enrollmentRepository.findByUuidAndDeletedAtIsNull(enrollmentUUID)
                                .flatMap(previousEnrollmentEntity -> {

                                    EnrollmentEntity updatedEntity = EnrollmentEntity
                                            .builder()
                                            .uuid(previousEnrollmentEntity.getUuid())
                                            .studentUUID(previousEnrollmentEntity.getStudentUUID())
                                            .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                                            .courseUUID(UUID.fromString(value.getFirst("courseUUID").trim()))
                                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                            .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                                            .semesterUUID(UUID.fromString(value.getFirst("semesterUUID").trim()))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .extra(Boolean.valueOf(value.getFirst("extra")))
                                            .isOpenLMS(Boolean.valueOf(value.getFirst("isOpenLMS")))
                                            .createdAt(previousEnrollmentEntity.getCreatedAt())
                                            .createdBy(previousEnrollmentEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousEnrollmentEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousEnrollmentEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousEnrollmentEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousEnrollmentEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousEnrollmentEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousEnrollmentEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousEnrollmentEntity.setDeletedBy(UUID.fromString(userId));
                                    previousEnrollmentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousEnrollmentEntity.setReqDeletedIP(reqIp);
                                    previousEnrollmentEntity.setReqDeletedPort(reqPort);
                                    previousEnrollmentEntity.setReqDeletedBrowser(reqBrowser);
                                    previousEnrollmentEntity.setReqDeletedOS(reqOs);
                                    previousEnrollmentEntity.setReqDeletedDevice(reqDevice);
                                    previousEnrollmentEntity.setReqDeletedReferer(reqReferer);

                                    //check student uuid exists
                                    return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
//                            check semester uuid exists
                                            .flatMap(studentEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSemesterUUID())
//                                    check subject offered exists
                                                            .flatMap(semesterEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubjectOfferedUUID())
                                                                    //check if campus exist in campuses
                                                                    .flatMap(subjectOfferedEntity -> campusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCampusUUID())
                                                                            //check if the given academic session and subject offered exists in Subject Offered Table
                                                                            .flatMap(campusEntity -> subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID(), updatedEntity.getSubjectOfferedUUID())
                                                                                    //fetch the campus Course Record from CampusCourse Handler
                                                                                    .flatMap(subjectOfferedEntity1 -> campusCourseRepository.findByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(updatedEntity.getCampusUUID(), updatedEntity.getCourseUUID())
                                                                                            //check if the student is registered in the entered Campus Course
                                                                                            .flatMap(campusCourse -> registrationRepository.findByStudentUUIDAndCampusCourseUUIDAndAcademicSessionUUIDAndDeletedAtIsNull(updatedEntity.getStudentUUID(), campusCourse.getUuid(), updatedEntity.getAcademicSessionUUID())
                                                                                                    .flatMap(registrationEntity -> {
                                                                                                        //check if extra is checked then store enrollment entity
                                                                                                        if (updatedEntity.getExtra()) {
                                                                                                            return enrollmentRepository.save(previousEnrollmentEntity)
                                                                                                                    .then(enrollmentRepository.save(updatedEntity))
                                                                                                                    .flatMap(enrollmentEntity -> responseSuccessMsg("Record Updated Successfully", enrollmentEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."));
                                                                                                        } else {
                                                                                                            //check if the student is already exist against the academic session and semester
                                                                                                            return enrollmentRepository.findFirstByStudentUUIDAndSubjectOfferedUUIDAndAcademicSessionUUIDAndSemesterUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getSubjectOfferedUUID(), updatedEntity.getAcademicSessionUUID(), updatedEntity.getSemesterUUID(), enrollmentUUID)
                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("The Student is Already Enrolled against the given semester and Subject Offered"))
                                                                                                                    .switchIfEmpty(Mono.defer(() -> enrollmentRepository.save(previousEnrollmentEntity)
                                                                                                                            .then(enrollmentRepository.save(updatedEntity))
                                                                                                                            .flatMap(enrollmentEntity -> responseSuccessMsg("Record Updated Successfully", enrollmentEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ));
                                                                                                        }
                                                                                                    }).switchIfEmpty(responseInfoMsg("The Entered Student Is Not Registered Against this campus Course"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("The Entered Student Is Not Registered Against this campus Course.Please Contact Developer."))
                                                                                            ).switchIfEmpty(responseInfoMsg("The Entered Campus And Course Does not Exist in Campus Course"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("The Entered Campus And Course Does not Exist in Campus Course.Please Contact Developer."))
                                                                                    ).switchIfEmpty(responseInfoMsg("Subject Offered Against the given Academic Session Does not Exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered Against the given Academic Session Does not Exist.Please Contact Developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Campus Does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Campus Does not exist.Please Contact Developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Subject Offered does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered does not exist. Please contact developer"))
                                                            ).switchIfEmpty(responseInfoMsg("Semester does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Semester does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Student does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student does not exist. Please contact developer"));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollments_is-open-lms_update")
    public Mono<ServerResponse> isOpenLMS(ServerRequest serverRequest) {
        UUID enrollmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    boolean isOpenLMS = Boolean.parseBoolean(value.getFirst("isOpenLMS"));
                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(enrollmentUUID)
                            .flatMap(enrollmentEntityDB -> {
                                // If isOpenLMS is not Boolean value
                                if (isOpenLMS != false && isOpenLMS != true) {
                                    return responseInfoMsg("Is Open LMS must be Active or InActive");
                                }

                                // If already same isOpenLMS exist in database.
                                if (((enrollmentEntityDB.getIsOpenLMS() ? true : false) == isOpenLMS)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                EnrollmentEntity updatedEnrollmentEntity = EnrollmentEntity.builder()
                                        .uuid(enrollmentEntityDB.getUuid())
                                        .status(enrollmentEntityDB.getStatus())
                                        .isOpenLMS(isOpenLMS == true ? true : false)
                                        .courseUUID(enrollmentEntityDB.getCourseUUID())
                                        .campusUUID(enrollmentEntityDB.getCampusUUID())
                                        .academicSessionUUID(enrollmentEntityDB.getAcademicSessionUUID())
                                        .studentUUID(enrollmentEntityDB.getStudentUUID())
                                        .subjectOfferedUUID(enrollmentEntityDB.getSubjectOfferedUUID())
                                        .semesterUUID(enrollmentEntityDB.getSemesterUUID())
                                        .extra(enrollmentEntityDB.getExtra())
                                        .createdAt(enrollmentEntityDB.getCreatedAt())
                                        .createdBy(enrollmentEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(enrollmentEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(enrollmentEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(enrollmentEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(enrollmentEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(enrollmentEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(enrollmentEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update isOpenLMS
                                enrollmentEntityDB.setDeletedBy(UUID.fromString(userId));
                                enrollmentEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                enrollmentEntityDB.setReqDeletedIP(reqIp);
                                enrollmentEntityDB.setReqDeletedPort(reqPort);
                                enrollmentEntityDB.setReqDeletedBrowser(reqBrowser);
                                enrollmentEntityDB.setReqDeletedOS(reqOs);
                                enrollmentEntityDB.setReqDeletedDevice(reqDevice);
                                enrollmentEntityDB.setReqDeletedReferer(reqReferer);

                                return enrollmentRepository.save(enrollmentEntityDB)
                                        .then(enrollmentRepository.save(updatedEnrollmentEntity))
                                        .flatMap(isOpenLMSUpdate -> responseSuccessMsg("Is Open LMS Updated Successfully", isOpenLMSUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update record.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollments_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID enrollmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(enrollmentUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                EnrollmentEntity entity = EnrollmentEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .courseUUID(previousEntity.getCourseUUID())
                                        .campusUUID(previousEntity.getCampusUUID())
                                        .academicSessionUUID(previousEntity.getAcademicSessionUUID())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .subjectOfferedUUID(previousEntity.getSubjectOfferedUUID())
                                        .semesterUUID(previousEntity.getSemesterUUID())
                                        .extra(previousEntity.getExtra())
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

                                return enrollmentRepository.save(previousEntity)
                                        .then(enrollmentRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_enrollments_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID enrollmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return enrollmentRepository.findByUuidAndDeletedAtIsNull(enrollmentUUID)
                //check if enrollment exists in timetable
                .flatMap(enrollmentEntity -> timetableCreationRepository.findFirstByEnrollmentUUIDAndDeletedAtIsNull(enrollmentEntity.getUuid())
                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference exists"))
                        .switchIfEmpty(Mono.defer(() -> {

                            enrollmentEntity.setDeletedBy(UUID.fromString(userId));
                            enrollmentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            enrollmentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            enrollmentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            enrollmentEntity.setReqDeletedIP(reqIp);
                            enrollmentEntity.setReqDeletedPort(reqPort);
                            enrollmentEntity.setReqDeletedBrowser(reqBrowser);
                            enrollmentEntity.setReqDeletedOS(reqOs);
                            enrollmentEntity.setReqDeletedDevice(reqDevice);
                            enrollmentEntity.setReqDeletedReferer(reqReferer);

                            return enrollmentRepository.save(enrollmentEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                        }))

                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
    }

//    --------------------- custom functions -------------------------------------

    /**
     * Fetch All Against Academic Session With Status filter
     **/
    public Mono<ServerResponse> indexAgainstSessionWithStatusFilter(UUID academicSessionUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexAllRecordsWithStatusAndAcademicSessionFilter(searchKeyWord, status, academicSessionUUID, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllByDeletedAtIsNullAndAcademicSessionAndStatus(academicSessionUUID, status, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * Fetch All Against Academic Session
     **/
    public Mono<ServerResponse> indexAgainstAcademicSession(UUID academicSessionUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexAllRecordsWithAcademicSessionFilter(academicSessionUUID, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithAcademicSessionAndDeletedAtIsNull(academicSessionUUID, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * Fetch All Against Enrollments Against Academic Session and Subjects (With Status Filter)
     **/
    public Mono<ServerResponse> indexAgainstSessionAndSubjectWithStatusFilter(UUID academicSessionUUID, UUID subjectUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexWithSubjectAndAcademicSessionWithStatusFilter(academicSessionUUID, subjectUUID, status, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithAcademicSessionAndSubjectAndStatusWhereDeletedAtIsNull(academicSessionUUID, subjectUUID, status, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * Fetch All Against Enrollments Against Academic Session and Subjects (Without Status Filter)
     **/
    public Mono<ServerResponse> indexAgainstSessionAndSubjectWithoutStatusFilter(UUID academicSessionUUID, UUID subjectUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexWithSubjectAndAcademicSessionFilter(academicSessionUUID, subjectUUID, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithAcademicSessionAndSubjectWhereDeletedAtIsNull(academicSessionUUID, subjectUUID, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * Fetch All Against Enrollments Subjects (With Status Filter)
     **/
    public Mono<ServerResponse> indexAgainstSubjectWithStatusFilter(UUID subjectUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexWithSubjectWithStatusFilter(subjectUUID, status, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithSubjectAndStatusWhereDeletedAtIsNull(subjectUUID, status, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * Fetch All Against Enrollments Subjects (Without Status Filter)
     **/
    public Mono<ServerResponse> indexAgainstSubjectWithoutStatusFilter(UUID subjectUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
                .indexWithSubjectFilter(subjectUUID, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
        return slaveEnrollmentFlux
                .collectList()
                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithSubjectWhereDeletedAtIsNull(subjectUUID, searchKeyWord)
                        .flatMap(count -> {
                            if (enrollmentEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


//    --------------------- custom functions -------------------------------------


    //
//    /**
//     * Fetch All AGAINST Course Subject With Status filter
//     **/
//    public Mono<ServerResponse> indexAgainstCourseSubjectWithStatusFilter(UUID courseSubject, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
//        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
//                .indexWithStatusAndCourseSubjectFilter(courseSubject, status, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
//        return slaveEnrollmentFlux
//                .collectList()
//                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithCourseSubjectAndStatusFilter(courseSubject, status, searchKeyWord)
//                        .flatMap(count -> {
//                            if (enrollmentEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    /**
//     * Fetch All AGAINST Course Subject
//     **/
//    public Mono<ServerResponse> indexAgainstCourseSubject(UUID courseSubjectUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {
//        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
//                .indexWithCourseSubjectFilter(courseSubjectUUID, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());
//        return slaveEnrollmentFlux
//                .collectList()
//                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithCourseSubjectFilter(courseSubjectUUID, searchKeyWord)
//                        .flatMap(count -> {
//                            if (enrollmentEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    /**
//     * Fetch All Enrollment Records Against Academic Session and Course Subject
//     **/
//    public Mono<ServerResponse> indexAgainstSessionAndCourseSubject(UUID courseSubjectUUID, UUID academicSessionUUID, String searchKeyWord, String directionProperty, String d, Pageable pageable) {
//        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
//                .indexWithAcademicSessionAndCourseSubjectFilter(courseSubjectUUID, academicSessionUUID, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//        return slaveEnrollmentFlux
//                .collectList()
//                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithSessionAndCourseSubjectFilter(academicSessionUUID, courseSubjectUUID, searchKeyWord)
//                        .flatMap(count -> {
//                            if (enrollmentEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    /**
//     * Fetch All Enrollment Records Against Academic Session and Course Subject and Status
//     **/
//    public Mono<ServerResponse> indexAgainstSessionCourseSubjectAndStatus(UUID courseSubjectUUID, UUID academicSessionUUID, Boolean status, String searchKeyWord, String directionProperty, String d, Pageable pageable) {
//        Flux<SlaveEnrollmentDto> slaveEnrollmentFlux = slaveEnrollmentRepository
//                .indexWithStatusAndAcademicSessionAndCourseSubjectFilter(courseSubjectUUID, academicSessionUUID, status, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//        return slaveEnrollmentFlux
//                .collectList()
//                .flatMap(enrollmentEntity -> slaveEnrollmentRepository.countAllRecordsWithSessionCourseSubjectAndStatusFilter(academicSessionUUID, courseSubjectUUID, status, searchKeyWord)
//                        .flatMap(count -> {
//                            if (enrollmentEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", enrollmentEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }

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
