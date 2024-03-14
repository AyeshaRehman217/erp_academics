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
import tuf.webscaf.app.dbContext.master.entity.CommencementOfClassesEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCommencementOfClassesDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCommencementOfClassesEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCommencementOfClassesRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "commencementOfClassesHandler")
@Component
public class CommencementOfClassesHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    CommencementOfClassesRepository commencementOfClassesRepository;

    @Autowired
    SlaveCommencementOfClassesRepository slaveCommencementOfClassesRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    LectureTypeRepository lectureTypeRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    LectureDeliveryModeRepository lectureDeliveryModeRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    StudentGroupRepository studentGroupRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    DayRepository dayRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_index")
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveCommencementOfClassesDto> slaveCommencementOfClassFlux = slaveCommencementOfClassesRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCommencementOfClassFlux
                    .collectList()
                    .flatMap(commencementEntityDB -> slaveCommencementOfClassesRepository
                            .countAllRecordsWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (commencementEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", commencementEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of campus uuid is present
        else {
            Flux<SlaveCommencementOfClassesDto> slaveCommencementOfClassFlux = slaveCommencementOfClassesRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCommencementOfClassFlux
                    .collectList()
                    .flatMap(commencementEntityDB -> slaveCommencementOfClassesRepository
                            .countAllRecordsWithoutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (commencementEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", commencementEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCommencementOfClassesRepository.showByUuid(timetableCreationUUID)
                .flatMap(classesEntity -> responseSuccessMsg("Record Fetched Successfully", classesEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_store")
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

                    UUID sectionUUID = null;
                    UUID enrollmentUUID = null;
                    UUID studentGroupUUID = null;

                    if ((value.containsKey("sectionUUID") && (!Objects.equals(value.getFirst("sectionUUID"), "")))) {
                        sectionUUID = UUID.fromString(value.getFirst("sectionUUID").trim());
                    }

                    if ((value.containsKey("enrollmentUUID") && (!Objects.equals(value.getFirst("enrollmentUUID"), "")))) {
                        enrollmentUUID = UUID.fromString(value.getFirst("enrollmentUUID").trim());
                    }

                    if ((value.containsKey("studentGroupUUID") && (!Objects.equals(value.getFirst("studentGroupUUID"), "")))) {
                        studentGroupUUID = UUID.fromString(value.getFirst("studentGroupUUID").trim());
                    }

                    CommencementOfClassesEntity commencementOfClassesEntity = CommencementOfClassesEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .description(value.getFirst("description").trim())
                            .startTime(LocalTime.parse(value.getFirst("startTime")))
                            .endTime(LocalTime.parse(value.getFirst("endTime")))
                            .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .rescheduled(Boolean.valueOf(value.getFirst("rescheduled")))
                            .rescheduledDate(LocalDateTime.parse(value.getFirst("rescheduledDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .enrollmentUUID(enrollmentUUID)
                            .sectionUUID(sectionUUID)
                            .studentGroupUUID(studentGroupUUID)
                            .priority(Integer.valueOf(value.getFirst("priority")))
                            .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                            .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
                            .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
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

                    //check if Start Time is before the End time
                    if (commencementOfClassesEntity.getStartTime().isAfter(commencementOfClassesEntity.getEndTime())) {
                        return responseInfoMsg("Start Time Should be Before the End Time");
                    }

                    //check if Start Time is before the End time
                    if (commencementOfClassesEntity.getEndTime().isBefore(commencementOfClassesEntity.getStartTime())) {
                        return responseInfoMsg("End Time Should be After the Start Time");
                    }

                    return studentRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getStudentUUID())
                            .flatMap(stdEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getTeacherUUID())
                                    .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getClassroomUUID())
                                            .flatMap(classroomEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getSubjectUUID())
                                                    .flatMap(subjectEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getLectureTypeUUID())
                                                            .flatMap(lectureType -> dayRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getDayUUID())
                                                                    .flatMap(dayEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getAcademicSessionUUID())
                                                                            .flatMap(academicSessionEntity -> {
                                                                                        // section, enrollment and student Group uuids are given with timetable
                                                                                        if (commencementOfClassesEntity.getSectionUUID() != null && commencementOfClassesEntity.getEnrollmentUUID() != null && commencementOfClassesEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                        }
                                                                                        // section and enrollment uuids are given with timetable
                                                                                        else if (commencementOfClassesEntity.getEnrollmentUUID() != null && commencementOfClassesEntity.getSectionUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and enrollment uuids are given with timetable
                                                                                        else if (commencementOfClassesEntity.getEnrollmentUUID() != null && commencementOfClassesEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and section uuids are given with timetable
                                                                                        else if (commencementOfClassesEntity.getSectionUUID() != null && commencementOfClassesEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                        }

                                                                                        // if Enrollment uuid is given with timetable
                                                                                        else if (commencementOfClassesEntity.getSectionUUID() != null) {
                                                                                            //Check if the entered enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                            return sectionRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getSectionUUID())
                                                                                                    .flatMap(studentGroup -> commencementOfClassesRepository.save(commencementOfClassesEntity)
                                                                                                            .flatMap(commencementEntityDB -> responseSuccessMsg("Record Stored Successfully", commencementEntityDB))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store record. There is something wrong please try again."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store record. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));
                                                                                        }

                                                                                        // if Enrollment uuid is given with timetable
                                                                                        else if (commencementOfClassesEntity.getEnrollmentUUID() != null) {
                                                                                            //Check if the entered enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                            return enrollmentRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getEnrollmentUUID())
                                                                                                    .flatMap(studentGroup -> commencementOfClassesRepository.save(commencementOfClassesEntity)
                                                                                                            .flatMap(commencementEntityDB -> responseSuccessMsg("Record Stored Successfully", commencementEntityDB))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store record. There is something wrong please try again."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store record. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));
                                                                                        }

                                                                                        // if student Group uuid is given with timetable
                                                                                        else if (commencementOfClassesEntity.getStudentGroupUUID() != null) {
                                                                                            //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                            return studentGroupRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesEntity.getStudentGroupUUID())
                                                                                                    .flatMap(studentGroup -> commencementOfClassesRepository.save(commencementOfClassesEntity)
                                                                                                            .flatMap(commencementEntityDB -> responseSuccessMsg("Record Stored Successfully", commencementEntityDB))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Store record. There is something wrong please try again."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store record. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Student Group Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Group Does not Exist.Please Contact Developer."));
                                                                                        } else {
                                                                                            return responseInfoMsg("Timetable must have one of Section or Enrollment UUID or Student Group");
                                                                                        }
                                                                                    }
                                                                            ).switchIfEmpty(responseInfoMsg("Academic Session Does not exist."))
                                                                            .onErrorResume(ex -> responseErrorMsg("Academic Session Does not exist.Please Contact Developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Day Does not exist."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Day Does not exist.Please Contact Developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Lecture Type Does not exist."))
                                                            .onErrorResume(ex -> responseErrorMsg("Lecture Type Does not exist.Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Subject Does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Subject Does not exist.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Classroom Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Classroom Does not exist.Please Contact Developer."))
                                    ).switchIfEmpty(responseInfoMsg("Teacher Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Does not exist.Please Contact Developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Student Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID commencementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(commencementUUID)
                        .flatMap(previousCommencementEntity -> {

                            UUID sectionUUID = null;
                            UUID enrollmentUUID = null;
                            UUID studentGroupUUID = null;

                            if ((value.containsKey("sectionUUID") && (!Objects.equals(value.getFirst("sectionUUID"), "")))) {
                                sectionUUID = UUID.fromString(value.getFirst("sectionUUID").trim());
                            }

                            if ((value.containsKey("enrollmentUUID") && (!Objects.equals(value.getFirst("enrollmentUUID"), "")))) {
                                enrollmentUUID = UUID.fromString(value.getFirst("enrollmentUUID").trim());
                            }

                            if ((value.containsKey("studentGroupUUID") && (!Objects.equals(value.getFirst("studentGroupUUID"), "")))) {
                                studentGroupUUID = UUID.fromString(value.getFirst("studentGroupUUID").trim());
                            }

                            CommencementOfClassesEntity updatedCommencementOfClasses = CommencementOfClassesEntity
                                    .builder()
                                    .uuid(previousCommencementEntity.getUuid())
                                    .description(value.getFirst("description").trim())
                                    .startTime(LocalTime.parse(value.getFirst("startTime")))
                                    .endTime(LocalTime.parse(value.getFirst("endTime")))
                                    .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                                    .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                                    .rescheduled(Boolean.valueOf(value.getFirst("rescheduled")))
                                    .rescheduledDate(LocalDateTime.parse(value.getFirst("rescheduledDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .enrollmentUUID(enrollmentUUID)
                                    .sectionUUID(sectionUUID)
                                    .studentGroupUUID(studentGroupUUID)
                                    .priority(Integer.valueOf(value.getFirst("priority")))
                                    .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                                    .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
                                    .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousCommencementEntity.getCreatedAt())
                                    .createdBy(previousCommencementEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousCommencementEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousCommencementEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousCommencementEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousCommencementEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousCommencementEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousCommencementEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //check if Start Time is before the End time
                            if (updatedCommencementOfClasses.getStartTime().isAfter(updatedCommencementOfClasses.getEndTime())) {
                                return responseInfoMsg("Start Time Should be Before the End Time");
                            }

                            //check if Start Time is before the End time
                            if (updatedCommencementOfClasses.getEndTime().isBefore(updatedCommencementOfClasses.getStartTime())) {
                                return responseInfoMsg("End Time Should be After the Start Time");
                            }

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousCommencementEntity.setDeletedBy(UUID.fromString(userId));
                            previousCommencementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));


                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getStudentUUID())
                                    .flatMap(stdEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getTeacherUUID())
                                            .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getClassroomUUID())
                                                    .flatMap(classroomEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getSubjectUUID())
                                                            .flatMap(subjectEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getLectureTypeUUID())
                                                                    .flatMap(lectureType -> dayRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getDayUUID())
                                                                            .flatMap(dayEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getAcademicSessionUUID())
                                                                                    .flatMap(academicSessionEntity -> {
                                                                                                // section, enrollment and student Group uuids are given with timetable
                                                                                                if (updatedCommencementOfClasses.getSectionUUID() != null && updatedCommencementOfClasses.getEnrollmentUUID() != null && updatedCommencementOfClasses.getStudentGroupUUID() != null) {
                                                                                                    return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                                }
                                                                                                // section and enrollment uuids are given with timetable
                                                                                                else if (updatedCommencementOfClasses.getEnrollmentUUID() != null && updatedCommencementOfClasses.getSectionUUID() != null) {
                                                                                                    return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                                }
                                                                                                // student Group and enrollment uuids are given with timetable
                                                                                                else if (updatedCommencementOfClasses.getEnrollmentUUID() != null && updatedCommencementOfClasses.getStudentGroupUUID() != null) {
                                                                                                    return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                                }
                                                                                                // student Group and section uuids are given with timetable
                                                                                                else if (updatedCommencementOfClasses.getSectionUUID() != null && updatedCommencementOfClasses.getStudentGroupUUID() != null) {
                                                                                                    return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                                }

                                                                                                // if Enrollment uuid is given with timetable
                                                                                                else if (updatedCommencementOfClasses.getSectionUUID() != null) {
                                                                                                    //Check if the entered enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                                    return sectionRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getSectionUUID())
                                                                                                            .flatMap(section -> commencementOfClassesRepository.save(previousCommencementEntity)
                                                                                                                    .then(commencementOfClassesRepository.save(updatedCommencementOfClasses))
                                                                                                                    .flatMap(commencementEntityDB -> responseSuccessMsg("Record Updated Successfully", commencementEntityDB))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));
                                                                                                }

                                                                                                // if Enrollment uuid is given with timetable
                                                                                                else if (updatedCommencementOfClasses.getEnrollmentUUID() != null) {
                                                                                                    //Check if the entered enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getEnrollmentUUID())
                                                                                                            .flatMap(enrollment -> commencementOfClassesRepository.save(previousCommencementEntity)
                                                                                                                    .then(commencementOfClassesRepository.save(updatedCommencementOfClasses))
                                                                                                                    .flatMap(commencementEntityDB -> responseSuccessMsg("Record Updated Successfully", commencementEntityDB))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));
                                                                                                }

                                                                                                // if student Group uuid is given with timetable
                                                                                                else if (updatedCommencementOfClasses.getStudentGroupUUID() != null) {
                                                                                                    //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                                    return studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedCommencementOfClasses.getStudentGroupUUID())
                                                                                                            .flatMap(studentGroup -> commencementOfClassesRepository.save(previousCommencementEntity)
                                                                                                                    .then(commencementOfClassesRepository.save(updatedCommencementOfClasses))
                                                                                                                    .flatMap(commencementEntityDB -> responseSuccessMsg("Record Updated Successfully", commencementEntityDB))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Student Group Does not Exist."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Student Group Does not Exist.Please Contact Developer."));
                                                                                                } else {
                                                                                                    return responseInfoMsg("Timetable must have one of Section or Enrollment UUID or Student Group");
                                                                                                }
                                                                                            }
                                                                                    ).switchIfEmpty(responseInfoMsg("Academic Session Does not exist."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Academic Session Does not exist.Please Contact Developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Day Does not exist."))
                                                                            .onErrorResume(ex -> responseErrorMsg("Day Does not exist.Please Contact Developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Lecture Type Does not exist."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Lecture Type Does not exist.Please Contact Developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Subject Does not exist."))
                                                            .onErrorResume(ex -> responseErrorMsg("Subject Does not exist.Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Classroom Does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Classroom Does not exist.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Teacher Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher Does not exist.Please Contact Developer."))
                                    ).switchIfEmpty(responseInfoMsg("Student Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Student Does not exist.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

//    public Mono<ServerResponse> store(ServerRequest serverRequest) {
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> {
//
//                    CommencementOfClassesEntity entity = CommencementOfClassesEntity
//                            .builder()
//                            .uuid(UUID.randomUUID())
//                            .startTime(LocalTime.parse(value.getFirst("startTime")))
//                            .endTime(LocalTime.parse(value.getFirst("endTime")))
//                            .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//
//                    //check if Start Time is before the End time
//                    if (entity.getStartTime().isAfter(entity.getEndTime())) {
//                        return responseInfoMsg("Start Time Should be Before the End Time");
//                    }
//
//                    //check if Start Time is before the End time
//                    if (entity.getEndTime().isBefore(entity.getStartTime())) {
//                        return responseInfoMsg("End Time Should be After the Start Time");
//                    }
//
//                    //check if the existing record exists in the Commencement Of Classes Table
//                    return commencementOfClassesRepository.findByCommencementOfClassesStartTimeAndEndTimeExists(entity.getStartTime(), entity.getEndTime(), entity.getDayUUID(), entity.getCreatedAt().toLocalDate())
//                            //check if the entered day , time and Created At (Date) record exists in timetable View
//                            .flatMap(checkCommencementClass -> responseInfoMsg("Record Already Exist Against this Start Time and End Time against the given Day"))
//                            .switchIfEmpty(Mono.defer(() -> commencementOfClassesRepository.fetchRecordFromTimeTableViewWithStartTimeEndTimeAndDay(entity.getStartTime(), entity.getEndTime(), entity.getDayUUID(), entity.getCreatedAt().toLocalDate())
//                                    .flatMap(timetableViewRecord -> {
//                                        //get Record from Timetable view and Store in Commencement Of Classes
//                                        entity.setDescription(timetableViewRecord.getDescription());
//                                        entity.setClassroomUUID(timetableViewRecord.getClassroomUUID());
//                                        entity.setAcademicSessionUUID(timetableViewRecord.getAcademicSessionUUID());
//                                        entity.setDayUUID(timetableViewRecord.getDayUUID());
//                                        entity.setEnrollmentUUID(timetableViewRecord.getEnrollmentUUID());
//                                        entity.setLectureDeliveryModeUUID(timetableViewRecord.getLectureDeliveryModeUUID());
//                                        entity.setLectureTypeUUID(timetableViewRecord.getLectureTypeUUID());
//                                        entity.setPriority(timetableViewRecord.getPriority());
//                                        entity.setRescheduled(timetableViewRecord.getRescheduled());
//                                        entity.setRescheduledDate(timetableViewRecord.getRescheduledDate());
//                                        entity.setSectionUUID(timetableViewRecord.getSectionUUID());
//                                        entity.setStudentGroupUUID(timetableViewRecord.getStudentGroupUUID());
//                                        entity.setStudentUUID(timetableViewRecord.getStudentUUID());
//                                        entity.setTeacherUUID(timetableViewRecord.getTeacherUUID());
//                                        entity.setSubjectUUID(timetableViewRecord.getSubjectUUID());
//
//                                        return commencementOfClassesRepository.save(entity)
//                                                .flatMap(commencementEntityDB -> responseSuccessMsg("Record Stored Successfully", commencementEntityDB))
//                                                .switchIfEmpty(responseInfoMsg("Unable to Stored record. There is something wrong please try again."))
//                                                .onErrorResume(ex -> responseErrorMsg("Unable to Stored record. Please contact developer."));
//                                    })
//                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record as No Class exists Against the Given Start,End Time and Day in Timetables"))
//                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record as No Class exists Against the Given Start,End Time and Day in Timetables.Please Contact Developer."))
//                            ));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }

//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID commencementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(commencementUUID)
//                        .flatMap(previousCommencementEntity -> {
//
//                            CommencementOfClassesEntity updatedCommencementOfClasses = CommencementOfClassesEntity
//                                    .builder()
//                                    .uuid(previousCommencementEntity.getUuid())
//                                    .description(value.getFirst("description").trim())
//                                    .startTime(LocalTime.parse(value.getFirst("startTime")))
//                                    .endTime(LocalTime.parse(value.getFirst("endTime")))
//                                    .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
//                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
//                                    .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
//                                    .academicSessionUUID(previousCommencementEntity.getAcademicSessionUUID())
//                                    .studentUUID(previousCommencementEntity.getStudentUUID())
//                                    .rescheduled(previousCommencementEntity.getRescheduled())
//                                    .rescheduledDate(previousCommencementEntity.getRescheduledDate())
//                                    .enrollmentUUID(previousCommencementEntity.getEnrollmentUUID())
//                                    .sectionUUID(previousCommencementEntity.getSectionUUID())
//                                    .studentGroupUUID(previousCommencementEntity.getStudentGroupUUID())
//                                    .priority(previousCommencementEntity.getPriority())
//                                    .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
//                                    .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
//                                    .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .createdAt(previousCommencementEntity.getCreatedAt())
//                                    .createdBy(previousCommencementEntity.getCreatedBy())
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .updatedBy(UUID.fromString(userId))
//                                    .build();
//
//                            //check if Start Time is before the End time
//                            if (updatedCommencementOfClasses.getStartTime().isAfter(updatedCommencementOfClasses.getEndTime())) {
//                                return responseInfoMsg("Start Time Should be Before the End Time");
//                            }
//
//                            //check if Start Time is before the End time
//                            if (updatedCommencementOfClasses.getEndTime().isBefore(updatedCommencementOfClasses.getStartTime())) {
//                                return responseInfoMsg("End Time Should be After the Start Time");
//                            }
//
//                            //Deleting Previous Record and Creating a New One Based on UUID
//                            previousCommencementEntity.setDeletedBy(UUID.fromString(userId));
//                            previousCommencementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//
//                            //check if the existing record exists in the Commencement Of Classes Table
//                            return commencementOfClassesRepository.findByCommencementOfClassesStartTimeAndEndTimeExistsAndUuidIsNot(updatedCommencementOfClasses.getStartTime(), updatedCommencementOfClasses.getEndTime(), updatedCommencementOfClasses.getDayUUID(), updatedCommencementOfClasses.getCreatedAt().toLocalDate(), commencementUUID)
//                                    //check if the entered day , time and Created At (Date) record exists in timetable View
//                                    .flatMap(checkCommencementClass -> responseInfoMsg("Record Already Exist Against this Start Time and End Time against the given Day"))
//                                    .switchIfEmpty(Mono.defer(() -> commencementOfClassesRepository.save(previousCommencementEntity)
//                                            .then(commencementOfClassesRepository.save(updatedCommencementOfClasses))
//                                            .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
//                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
//                                    ));
//                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
//    }

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID commencementOfClassesUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
        return serverRequest
                .formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));

                    return commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(commencementOfClassesUUID)
                            .flatMap(previousCommencementEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousCommencementEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CommencementOfClassesEntity updatedCommencementOfClasses = CommencementOfClassesEntity
                                        .builder()
                                        .uuid(previousCommencementEntity.getUuid())
                                        .description(previousCommencementEntity.getDescription())
                                        .startTime(previousCommencementEntity.getStartTime())
                                        .endTime(previousCommencementEntity.getEndTime())
                                        .academicSessionUUID(previousCommencementEntity.getAcademicSessionUUID())
                                        .subjectUUID(previousCommencementEntity.getSubjectUUID())
                                        .teacherUUID(previousCommencementEntity.getTeacherUUID())
                                        .classroomUUID(previousCommencementEntity.getClassroomUUID())
                                        .studentUUID(previousCommencementEntity.getStudentUUID())
                                        .rescheduled(previousCommencementEntity.getRescheduled())
                                        .rescheduledDate(previousCommencementEntity.getRescheduledDate())
                                        .enrollmentUUID(previousCommencementEntity.getEnrollmentUUID())
                                        .sectionUUID(previousCommencementEntity.getSectionUUID())
                                        .studentGroupUUID(previousCommencementEntity.getStudentGroupUUID())
                                        .priority(previousCommencementEntity.getPriority())
                                        .lectureTypeUUID(previousCommencementEntity.getLectureTypeUUID())
                                        .lectureDeliveryModeUUID(previousCommencementEntity.getLectureDeliveryModeUUID())
                                        .dayUUID(previousCommencementEntity.getDayUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousCommencementEntity.getCreatedAt())
                                        .createdBy(previousCommencementEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousCommencementEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousCommencementEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousCommencementEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousCommencementEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousCommencementEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousCommencementEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousCommencementEntity.setDeletedBy(UUID.fromString(userId));
                                previousCommencementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousCommencementEntity.setReqDeletedIP(reqIp);
                                previousCommencementEntity.setReqDeletedPort(reqPort);
                                previousCommencementEntity.setReqDeletedBrowser(reqBrowser);
                                previousCommencementEntity.setReqDeletedOS(reqOs);
                                previousCommencementEntity.setReqDeletedDevice(reqDevice);
                                previousCommencementEntity.setReqDeletedReferer(reqReferer);

                                return commencementOfClassesRepository.save(previousCommencementEntity)
                                        .then(commencementOfClassesRepository.save(updatedCommencementOfClasses))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_commencement-of-classes_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return commencementOfClassesRepository.findByUuidAndDeletedAtIsNull(timetableCreationUUID)
                .flatMap(commencementEntity -> {

                    commencementEntity.setDeletedBy(UUID.fromString(userId));
                    commencementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    commencementEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    commencementEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    commencementEntity.setReqDeletedIP(reqIp);
                    commencementEntity.setReqDeletedPort(reqPort);
                    commencementEntity.setReqDeletedBrowser(reqBrowser);
                    commencementEntity.setReqDeletedOS(reqOs);
                    commencementEntity.setReqDeletedDevice(reqDevice);
                    commencementEntity.setReqDeletedReferer(reqReferer);

                    return commencementOfClassesRepository.save(commencementEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
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
