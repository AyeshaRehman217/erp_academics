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
import tuf.webscaf.app.dbContext.master.entity.TimetableRescheduleEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTimetableCreationDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTimetableRescheduleRepository;
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

@Tag(name = "timetableRescheduleHandler")
@Component
public class TimetableRescheduleHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TimetableRescheduleRepository timetableRescheduleRepository;

    @Autowired
    SlaveTimetableRescheduleRepository slaveTimetableRescheduleRepository;

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
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    StudentGroupRepository studentGroupRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_index")
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

        if (!subjectUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableRescheduleRepository
                    .indexWithStatusAgainstSubject(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableRescheduleRepository
                            .countTimetableRecordWithStatusAgainstSubject(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (timetableCreationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableCreationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!subjectUUID.isEmpty()) {
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableRescheduleRepository
                    .indexWithoutStatusAgainstSubject(UUID.fromString(subjectUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableRescheduleRepository
                            .countTimetableRecordWithoutStatusAgainstSubject(UUID.fromString(subjectUUID), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (timetableCreationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableCreationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableRescheduleRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableRescheduleRepository
                            .countTimetableRecordWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (timetableCreationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableCreationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableRescheduleRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableRescheduleRepository
                            .countTimetableRecordWithoutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (timetableCreationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableCreationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID timetableUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTimetableRescheduleRepository.findByUuidAndDeletedAtIsNull(timetableUUID)
                .flatMap(TimetableRescheduleEntity -> responseSuccessMsg("Record Fetched Successfully", TimetableRescheduleEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {
        String userId = serverRequest.headers().firstHeader("auid");

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

                    UUID sectionUUID = null;
                    UUID enrollmentUUID = null;
                    UUID studentGroupUUID = null;

                    if (value.containsKey("sectionUUID") && (!Objects.equals(value.getFirst("sectionUUID"), ""))) {
                        sectionUUID = UUID.fromString(value.getFirst("sectionUUID").trim());
                    }

                    if (value.containsKey("enrollmentUUID") && (!Objects.equals(value.getFirst("enrollmentUUID"), ""))) {
                        enrollmentUUID = UUID.fromString(value.getFirst("enrollmentUUID").trim());
                    }

                    if (value.containsKey("studentGroupUUID") && (!Objects.equals(value.getFirst("studentGroupUUID"), ""))) {
                        studentGroupUUID = UUID.fromString(value.getFirst("studentGroupUUID").trim());
                    }


                    TimetableRescheduleEntity timetableRescheduleEntityRecord = TimetableRescheduleEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .description(value.getFirst("description").trim())
                            .rescheduledDate(LocalDateTime.parse(value.getFirst("rescheduledDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .startTime(LocalTime.parse(value.getFirst("startTime")))
                            .endTime(LocalTime.parse(value.getFirst("endTime")))
                            .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .enrollmentUUID(enrollmentUUID)
                            .sectionUUID(sectionUUID)
                            .studentGroupUUID(studentGroupUUID)
                            .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                            .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
                            .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .rescheduled(true)
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .build();

                    //check if Start Time is before the End time
                    if (timetableRescheduleEntityRecord.getStartTime().isAfter(timetableRescheduleEntityRecord.getEndTime())) {
                        return responseInfoMsg("Start Time Should be Before the End Time");
                    }

                    //check if Start Time is before the End time
                    if (timetableRescheduleEntityRecord.getEndTime().isBefore(timetableRescheduleEntityRecord.getStartTime())) {
                        return responseInfoMsg("End Time Should be After the Start Time");
                    }

                    // check if academic session uuid exists
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getAcademicSessionUUID())
                            .flatMap(academicSessionEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getSubjectUUID())
                                    .flatMap(subjectEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getTeacherUUID())
                                            .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getClassroomUUID())
                                                    .flatMap(classroomEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getLectureTypeUUID())
                                                            .flatMap(lectureTypeEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getLectureDeliveryModeUUID())
                                                                    // Check name is unique
                                                                    .flatMap(lectureDeliveryModeEntity -> {
                                                                                // section, enrollment and student Group uuids are given with timetable
                                                                                if (timetableRescheduleEntityRecord.getSectionUUID() != null && timetableRescheduleEntityRecord.getEnrollmentUUID() != null && timetableRescheduleEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                }
                                                                                // section and enrollment uuids are given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getEnrollmentUUID() != null && timetableRescheduleEntityRecord.getSectionUUID() != null) {
                                                                                    return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                }
                                                                                // student Group and enrollment uuids are given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getEnrollmentUUID() != null && timetableRescheduleEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                }
                                                                                // student Group and section uuids are given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getSectionUUID() != null && timetableRescheduleEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                }

                                                                                // if section uuid is given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getSectionUUID() != null) {
                                                                                    //Check if the entered Section is Already Allocated in the Given Start time and end time in the same day
                                                                                    return sectionRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getSectionUUID())
                                                                                            .flatMap(section -> timetableRescheduleRepository.findBySectionAlreadyExistAgainstDayAndTimeAndAcademicSession
                                                                                                            (timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID(), timetableRescheduleEntityRecord.getSectionUUID())
                                                                                                    .flatMap(checkSectionMsg -> responseInfoMsg("The Entered Section is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                    //check if the section is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs
                                                                                                                    (timetableRescheduleEntityRecord.getSectionUUID(), timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getSubjectUUID(), timetableRescheduleEntityRecord.getClassroomUUID(), timetableRescheduleEntityRecord.getTeacherUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID())
                                                                                                            // save timetable record
                                                                                                            .flatMap(checkSection -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                                                                                    ))
                                                                                            ).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));
                                                                                }

                                                                                // if enrollment uuid is given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getEnrollmentUUID() != null) {
                                                                                    //Check if the entered Enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getEnrollmentUUID())
                                                                                            .flatMap(enrollment -> {
                                                                                                        //check if enrollment subject and entered subject matches or not
                                                                                                        return subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getAcademicSessionUUID(), enrollment.getSubjectOfferedUUID())
                                                                                                                .flatMap(subjectOffered -> timetableRescheduleRepository.findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTime
                                                                                                                                (timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID(), timetableRescheduleEntityRecord.getEnrollmentUUID())
                                                                                                                        .flatMap(checkEnrollmentMsg -> responseInfoMsg("The Entered Enrollment is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                                        //check if the enrollment is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                                        .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs
                                                                                                                                        (timetableRescheduleEntityRecord.getEnrollmentUUID(), timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getSubjectUUID(), timetableRescheduleEntityRecord.getClassroomUUID(), timetableRescheduleEntityRecord.getTeacherUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID())
                                                                                                                                // save timetable record
                                                                                                                                .flatMap(checkEnrollment -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                                        .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                                ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                                        .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                                                                                                        )))
                                                                                                                .switchIfEmpty(responseInfoMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session."))
                                                                                                                .onErrorResume(ex -> responseErrorMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session.Please Contact Developer."));
                                                                                                    }
                                                                                            )
                                                                                            .switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));
                                                                                }

                                                                                // if student Group uuid is given with timetable
                                                                                else if (timetableRescheduleEntityRecord.getStudentGroupUUID() != null) {
                                                                                    //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                    return studentGroupRepository.findByUuidAndDeletedAtIsNull(timetableRescheduleEntityRecord.getStudentGroupUUID())
                                                                                            .flatMap(studentGroup -> timetableRescheduleRepository.findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTime
                                                                                                            (timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID(), timetableRescheduleEntityRecord.getStudentGroupUUID())
                                                                                                    .flatMap(checkStudentGroupMsg -> responseInfoMsg("The Entered Student Group is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                    //check if the Student Group is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIs
                                                                                                                    (timetableRescheduleEntityRecord.getStudentGroupUUID(), timetableRescheduleEntityRecord.getDayUUID(), timetableRescheduleEntityRecord.getSubjectUUID(), timetableRescheduleEntityRecord.getClassroomUUID(), timetableRescheduleEntityRecord.getTeacherUUID(), timetableRescheduleEntityRecord.getStartTime(), timetableRescheduleEntityRecord.getEndTime(), timetableRescheduleEntityRecord.getAcademicSessionUUID())
                                                                                                            // save timetable record
                                                                                                            .flatMap(checkStudentGroup -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(timetableRescheduleEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                                                                                    ))
                                                                                            ).switchIfEmpty(responseInfoMsg("Student Group Does not Exist."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Student Group Does not Exist.Please Contact Developer."));
                                                                                } else {
                                                                                    return responseInfoMsg("Timetable must have one of Section or Enrollment UUID or Student Group");
                                                                                }
                                                                            }
                                                                    ).switchIfEmpty(responseInfoMsg("Lecture Delivery Mode record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Lecture Delivery Mode record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Lecture Type record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Lecture Type record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Classroom record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Classroom record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID timetableUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> timetableRescheduleRepository.findByUuidAndDeletedAtIsNull(timetableUUID)
                        .flatMap(previousTimetableRescheduleEntity -> {


                            UUID sectionUUID = null;
                            UUID enrollmentUUID = null;
                            UUID studentGroupUUID = null;

                            if (value.containsKey("sectionUUID") && (!Objects.equals(value.getFirst("sectionUUID"), ""))) {
                                sectionUUID = UUID.fromString(value.getFirst("sectionUUID").trim());
                            }

                            if (value.containsKey("enrollmentUUID") && (!Objects.equals(value.getFirst("enrollmentUUID"), ""))) {
                                enrollmentUUID = UUID.fromString(value.getFirst("enrollmentUUID").trim());
                            }

                            if (value.containsKey("studentGroupUUID") && (!Objects.equals(value.getFirst("studentGroupUUID"), ""))) {
                                studentGroupUUID = UUID.fromString(value.getFirst("studentGroupUUID").trim());
                            }


                            TimetableRescheduleEntity updatedTimetableRescheduleEntity = TimetableRescheduleEntity
                                    .builder()
                                    .uuid(previousTimetableRescheduleEntity.getUuid())
                                    .description(value.getFirst("description").trim())
                                    .rescheduledDate(LocalDateTime.parse(value.getFirst("rescheduledDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .startTime(LocalTime.parse(value.getFirst("startTime")))
                                    .endTime(LocalTime.parse(value.getFirst("endTime")))
                                    .subjectUUID(UUID.fromString(value.getFirst("subjectUUID").trim()))
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                                    .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                    .enrollmentUUID(enrollmentUUID)
                                    .sectionUUID(sectionUUID)
                                    .studentGroupUUID(studentGroupUUID)
                                    .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
                                    .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
                                    .dayUUID(UUID.fromString(value.getFirst("dayUUID").trim()))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .rescheduled(true)
                                    .createdAt(previousTimetableRescheduleEntity.getCreatedAt())
                                    .createdBy(previousTimetableRescheduleEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .build();

                            // update status
                            previousTimetableRescheduleEntity.setDeletedBy(UUID.fromString(userId));
                            previousTimetableRescheduleEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));


                            //check if Start Time is before the End time
                            if (updatedTimetableRescheduleEntity.getStartTime().isAfter(updatedTimetableRescheduleEntity.getEndTime())) {
                                return responseInfoMsg("Start Time Should be Before the End Time");
                            }

                            //check if Start Time is before the End time
                            if (updatedTimetableRescheduleEntity.getEndTime().isBefore(updatedTimetableRescheduleEntity.getStartTime())) {
                                return responseInfoMsg("End Time Should be After the Start Time");
                            }

                            // check if academic session uuid exists
                            return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getAcademicSessionUUID())
                                    .flatMap(academicSessionEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getSubjectUUID())
                                            .flatMap(subjectEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getTeacherUUID())
                                                    .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getClassroomUUID())
                                                            .flatMap(classroomEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getLectureTypeUUID())
                                                                    .flatMap(lectureTypeEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getLectureDeliveryModeUUID())
                                                                            .flatMap(lectureDeliveryModeEntity -> {
                                                                                        // section, enrollment and student Group uuids are given with timetable
                                                                                        if (updatedTimetableRescheduleEntity.getSectionUUID() != null && updatedTimetableRescheduleEntity.getEnrollmentUUID() != null && updatedTimetableRescheduleEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                        }
                                                                                        // section and enrollment uuids are given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getEnrollmentUUID() != null && updatedTimetableRescheduleEntity.getSectionUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and enrollment uuids are given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getEnrollmentUUID() != null && updatedTimetableRescheduleEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and section uuids are given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getSectionUUID() != null && updatedTimetableRescheduleEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                        }
                                                                                        // if section uuid is given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getSectionUUID() != null) {
                                                                                            //Check if the entered Section is Already Allocated in the Given Start time and end time in the same day
                                                                                            return sectionRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getSectionUUID())
                                                                                                    .flatMap(section -> timetableRescheduleRepository.findBySectionAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                    (timetableUUID, updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(), updatedTimetableRescheduleEntity.getAcademicSessionUUID(), updatedTimetableRescheduleEntity.getSectionUUID())
                                                                                                            .flatMap(checkSectionMsg -> responseInfoMsg("The Entered Section is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                            //check if the section is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                            (timetableUUID, updatedTimetableRescheduleEntity.getSectionUUID(), updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getSubjectUUID(), updatedTimetableRescheduleEntity.getClassroomUUID(), updatedTimetableRescheduleEntity.getTeacherUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(), updatedTimetableRescheduleEntity.getAcademicSessionUUID())
                                                                                                                    // save timetable record
                                                                                                                    .flatMap(checkSection -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                            .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                            .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))))
                                                                                                            ))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));

                                                                                        }

                                                                                        // if enrollment uuid is given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getEnrollmentUUID() != null) {
                                                                                            //Check if the entered Enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                            return enrollmentRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getEnrollmentUUID())
                                                                                                    .flatMap(enrollment -> {
                                                                                                                //check if enrollment subject and entered subject matches or not
                                                                                                                return subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getAcademicSessionUUID(), enrollment.getSubjectOfferedUUID())
                                                                                                                        .flatMap(subjectOffered -> timetableRescheduleRepository.findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                                        (timetableUUID, updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(),updatedTimetableRescheduleEntity.getAcademicSessionUUID(),updatedTimetableRescheduleEntity.getEnrollmentUUID())
                                                                                                                                .flatMap(checkEnrollmentMsg -> responseInfoMsg("The Entered Enrollment is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                                                //check if the enrollment is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                                                .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                                                (timetableUUID, updatedTimetableRescheduleEntity.getEnrollmentUUID(), updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getSubjectUUID(), updatedTimetableRescheduleEntity.getClassroomUUID(), updatedTimetableRescheduleEntity.getTeacherUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(),updatedTimetableRescheduleEntity.getAcademicSessionUUID())
                                                                                                                                        // save timetable record
                                                                                                                                        .flatMap(checkEnrollment -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                                                .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                                                .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                                                .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                                        ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                                                .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                                                .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                                                .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))))
                                                                                                                                ))).switchIfEmpty(responseInfoMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session."))
                                                                                                                        .onErrorResume(ex -> responseErrorMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session.Please Contact Developer."));
                                                                                                            }
                                                                                                    ).switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));

                                                                                        }

                                                                                        // if student Group uuid is given with timetable
                                                                                        else if (updatedTimetableRescheduleEntity.getStudentGroupUUID() != null) {
                                                                                            //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                            return studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedTimetableRescheduleEntity.getStudentGroupUUID())
                                                                                                    //check if Student group is already assigned for given day between start time and end time
                                                                                                    .flatMap(studentGroup -> timetableRescheduleRepository.findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                    (timetableUUID, updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(),updatedTimetableRescheduleEntity.getAcademicSessionUUID(),updatedTimetableRescheduleEntity.getStudentGroupUUID())
                                                                                                            .flatMap(checkStudentGroupMsg -> responseInfoMsg("The Entered Student Group is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                            //check if the Student Group is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                            (timetableUUID, updatedTimetableRescheduleEntity.getStudentGroupUUID(), updatedTimetableRescheduleEntity.getDayUUID(), updatedTimetableRescheduleEntity.getSubjectUUID(), updatedTimetableRescheduleEntity.getClassroomUUID(), updatedTimetableRescheduleEntity.getTeacherUUID(), updatedTimetableRescheduleEntity.getStartTime(), updatedTimetableRescheduleEntity.getEndTime(),updatedTimetableRescheduleEntity.getAcademicSessionUUID())
                                                                                                                    // save timetable record
                                                                                                                    .flatMap(checkStudentGroup -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                            .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ).switchIfEmpty(Mono.defer(() -> timetableRescheduleRepository.save(previousTimetableRescheduleEntity)
                                                                                                                            .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to update record. Please contact developer."))))
                                                                                                            ))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Student Group Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Group Does not Exist.Please Contact Developer."));
                                                                                        } else {
                                                                                            return responseInfoMsg("Timetable must have one of Section or Enrollment UUID or Student Group");
                                                                                        }
                                                                                    }
                                                                            ).switchIfEmpty(responseInfoMsg("Lecture Delivery Mode record does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Lecture Delivery Mode record does not exist. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Lecture Type record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Lecture Type record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Classroom record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Classroom record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID timetableUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

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
                    return timetableRescheduleRepository.findByUuidAndDeletedAtIsNull(timetableUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TimetableRescheduleEntity updatedTimetableRescheduleEntity = TimetableRescheduleEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .rescheduledDate(previousEntity.getRescheduledDate())
                                        .rescheduled(previousEntity.getRescheduled())
                                        .description(previousEntity.getDescription())
                                        .startTime(previousEntity.getStartTime())
                                        .endTime(previousEntity.getEndTime())
                                        .subjectUUID(previousEntity.getSubjectUUID())
                                        .teacherUUID(previousEntity.getTeacherUUID())
                                        .classroomUUID(previousEntity.getClassroomUUID())
                                        .academicSessionUUID(previousEntity.getAcademicSessionUUID())
                                        .enrollmentUUID(previousEntity.getEnrollmentUUID())
                                        .sectionUUID(previousEntity.getSectionUUID())
                                        .studentGroupUUID(previousEntity.getStudentGroupUUID())
                                        .lectureTypeUUID(previousEntity.getLectureTypeUUID())
                                        .lectureDeliveryModeUUID(previousEntity.getLectureDeliveryModeUUID())
                                        .dayUUID(previousEntity.getDayUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedAt(previousEntity.getUpdatedAt())
                                        .updatedBy(previousEntity.getUpdatedBy())
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));

                                return timetableRescheduleRepository.save(previousEntity)
                                        .then(timetableRescheduleRepository.save(updatedTimetableRescheduleEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-reschedules_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID timetableUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return timetableRescheduleRepository.findByUuidAndDeletedAtIsNull(timetableUUID)
                .flatMap(TimetableRescheduleEntity -> {

                    TimetableRescheduleEntity.setDeletedBy(UUID.fromString(userId));
                    TimetableRescheduleEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));

                    return timetableRescheduleRepository.save(TimetableRescheduleEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    //    --------------------------------- End of Custom Functions ----------------------------------------
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
