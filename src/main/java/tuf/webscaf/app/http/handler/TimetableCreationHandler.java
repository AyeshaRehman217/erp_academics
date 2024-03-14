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
import tuf.webscaf.app.dbContext.master.entity.TimetableCreationEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTimetableCreationDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTimetableCreationRepository;
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

@Tag(name = "timetableCreationHandler")
@Component
public class TimetableCreationHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    SlaveTimetableCreationRepository slaveTimetableCreationRepository;

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

    @AuthHasPermission(value = "academic_api_v1_timetable-creations_index")
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
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableCreationRepository
                    .indexWithStatusAgainstSubject(UUID.fromString(subjectUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableCreationRepository
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
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableCreationRepository
                    .indexWithoutStatusAgainstSubject(UUID.fromString(subjectUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableCreationRepository
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
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableCreationRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableCreationRepository
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
            Flux<SlaveTimetableCreationDto> slaveTimetableCreationFlux = slaveTimetableCreationRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTimetableCreationFlux
                    .collectList()
                    .flatMap(timetableCreationEntity -> slaveTimetableCreationRepository
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

    @AuthHasPermission(value = "academic_api_v1_timetable-creations_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTimetableCreationRepository.findByUuidAndDeletedAtIsNull(timetableCreationUUID)
                .flatMap(timetableCreationEntity -> responseSuccessMsg("Record Fetched Successfully", timetableCreationEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-creations_store")
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

                    if ((value.containsKey("sectionUUID") && (!Objects.equals(value.getFirst("sectionUUID"), "")))) {
                        sectionUUID = UUID.fromString(value.getFirst("sectionUUID").trim());
                    }

                    if ((value.containsKey("enrollmentUUID") && (!Objects.equals(value.getFirst("enrollmentUUID"), "")))) {
                        enrollmentUUID = UUID.fromString(value.getFirst("enrollmentUUID").trim());
                    }

                    if ((value.containsKey("studentGroupUUID") && (!Objects.equals(value.getFirst("studentGroupUUID"), "")))) {
                        studentGroupUUID = UUID.fromString(value.getFirst("studentGroupUUID").trim());
                    }


                    TimetableCreationEntity timeTableCreationEntityRecord = TimetableCreationEntity.builder()
                            .uuid(UUID.randomUUID())
                            .description(value.getFirst("description").trim())
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
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .build();

                    //check if Start Time is before the End time
                    if (timeTableCreationEntityRecord.getStartTime().isAfter(timeTableCreationEntityRecord.getEndTime())) {
                        return responseInfoMsg("Start Time Should be Before the End Time");
                    }

                    //check if Start Time is before the End time
                    if (timeTableCreationEntityRecord.getEndTime().isBefore(timeTableCreationEntityRecord.getStartTime())) {
                        return responseInfoMsg("End Time Should be After the Start Time");
                    }

                    // check if academic session uuid exists
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getAcademicSessionUUID())
                            // check if subject uuid exists
                            .flatMap(academicSessionEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getSubjectUUID())
                                    // check if teacher uuid exists
                                    .flatMap(subjectEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getTeacherUUID())
                                            // check if classroom uuid exists
                                            .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getClassroomUUID())
                                                    // check if lecture Type uuid exists
                                                    .flatMap(classroomEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getLectureTypeUUID())
                                                            // check if lecture Delivery Mode uuid exists
                                                            .flatMap(lectureTypeEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getLectureDeliveryModeUUID())
                                                                    .flatMap(lectureDeliveryModeEntity -> {
                                                                                // section, enrollment and student Group uuids are given with timetable
                                                                                if (timeTableCreationEntityRecord.getSectionUUID() != null && timeTableCreationEntityRecord.getEnrollmentUUID() != null && timeTableCreationEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                }
                                                                                // section and enrollment uuids are given with timetable
                                                                                else if (timeTableCreationEntityRecord.getEnrollmentUUID() != null && timeTableCreationEntityRecord.getSectionUUID() != null) {
                                                                                    return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                }
                                                                                // student Group and enrollment uuids are given with timetable
                                                                                else if (timeTableCreationEntityRecord.getEnrollmentUUID() != null && timeTableCreationEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                }
                                                                                // student Group and section uuids are given with timetable
                                                                                else if (timeTableCreationEntityRecord.getSectionUUID() != null && timeTableCreationEntityRecord.getStudentGroupUUID() != null) {
                                                                                    return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                }

                                                                                // if section uuid is given with timetable
                                                                                else if (timeTableCreationEntityRecord.getSectionUUID() != null) {
                                                                                    //Check if the entered Section is Already Allocated in the Given Start time and end time in the same day and academic Session
                                                                                    return sectionRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getSectionUUID())
                                                                                            .flatMap(section -> timetableCreationRepository.findBySectionAlreadyExistAgainstDayAndTimeAndAcademicSession
                                                                                                            (timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID(), timeTableCreationEntityRecord.getSectionUUID())
                                                                                                    .flatMap(checkSectionMsg -> responseInfoMsg("The Entered Section is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                    //check if the section is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs
                                                                                                                    (timeTableCreationEntityRecord.getSectionUUID(), timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getSubjectUUID(), timeTableCreationEntityRecord.getClassroomUUID(), timeTableCreationEntityRecord.getTeacherUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID())
                                                                                                            // save timetable record
                                                                                                            .flatMap(checkSection -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ))
                                                                                                    ))).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));
                                                                                }

                                                                                // if enrollment uuid is given with timetable
                                                                                else if (timeTableCreationEntityRecord.getEnrollmentUUID() != null) {
                                                                                    //Check if the entered Enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                    return enrollmentRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getEnrollmentUUID())
                                                                                            .flatMap(enrollment -> {
                                                                                                        //check if enrollment subject and entered subject matches or not
                                                                                                        return subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getAcademicSessionUUID(), enrollment.getSubjectOfferedUUID())
                                                                                                                .flatMap(subjectOffered -> timetableCreationRepository.findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTime
                                                                                                                                (timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID(), timeTableCreationEntityRecord.getEnrollmentUUID())
                                                                                                                        .flatMap(checkEnrollmentMsg -> responseInfoMsg("The Entered Enrollment is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                                        //check if the enrollment is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                                        .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs
                                                                                                                                        (timeTableCreationEntityRecord.getEnrollmentUUID(), timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getSubjectUUID(), timeTableCreationEntityRecord.getClassroomUUID(), timeTableCreationEntityRecord.getTeacherUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID())
                                                                                                                                // save timetable record
                                                                                                                                .flatMap(checkEnrollment -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                                        .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                                ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                                        .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                                ))
                                                                                                                        ))
                                                                                                                ).switchIfEmpty(responseInfoMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session."))
                                                                                                                .onErrorResume(ex -> responseErrorMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session.Please Contact Developer."));
                                                                                                    }
                                                                                            ).switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));
                                                                                }

                                                                                // if student Group uuid is given with timetable
                                                                                else if (timeTableCreationEntityRecord.getStudentGroupUUID() != null) {
                                                                                    //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                    return studentGroupRepository.findByUuidAndDeletedAtIsNull(timeTableCreationEntityRecord.getStudentGroupUUID())
                                                                                            .flatMap(studentGroup -> timetableCreationRepository.findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTime
                                                                                                            (timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID(), timeTableCreationEntityRecord.getStudentGroupUUID())
                                                                                                    .flatMap(checkStudentGroupMsg -> responseInfoMsg("The Entered Student Group is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                    //check if the Student Group is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIs
                                                                                                                    (timeTableCreationEntityRecord.getStudentGroupUUID(), timeTableCreationEntityRecord.getDayUUID(), timeTableCreationEntityRecord.getSubjectUUID(), timeTableCreationEntityRecord.getClassroomUUID(), timeTableCreationEntityRecord.getTeacherUUID(), timeTableCreationEntityRecord.getStartTime(), timeTableCreationEntityRecord.getEndTime(), timeTableCreationEntityRecord.getAcademicSessionUUID())
                                                                                                            // save timetable record
                                                                                                            .flatMap(checkStudentGroup -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(timeTableCreationEntityRecord)
                                                                                                                    .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", saveTimetableCreationEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            ))
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


    @AuthHasPermission(value = "academic_api_v1_timetable-creations_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> timetableCreationRepository.findByUuidAndDeletedAtIsNull(timetableCreationUUID)
                        .flatMap(previousTimetableCreationEntity -> {
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


                            TimetableCreationEntity updatedTimetableCreationEntity = TimetableCreationEntity
                                    .builder()
                                    .uuid(previousTimetableCreationEntity.getUuid())
                                    .description(value.getFirst("description").trim())
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
                                    .createdAt(previousTimetableCreationEntity.getCreatedAt())
                                    .createdBy(previousTimetableCreationEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .build();

                            // update status
                            previousTimetableCreationEntity.setDeletedBy(UUID.fromString(userId));
                            previousTimetableCreationEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));

                            //check if Start Time is before the End time
                            if (updatedTimetableCreationEntity.getStartTime().isAfter(updatedTimetableCreationEntity.getEndTime())) {
                                return responseInfoMsg("Start Time Should be Before the End Time");
                            }

                            //check if Start Time is before the End time
                            if (updatedTimetableCreationEntity.getEndTime().isBefore(updatedTimetableCreationEntity.getStartTime())) {
                                return responseInfoMsg("End Time Should be After the Start Time");
                            }

                            // check if academic session uuid exists
                            return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getAcademicSessionUUID())
                                    // check if subject uuid exists
                                    .flatMap(academicSessionEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getSubjectUUID())
                                            // check if teacher uuid exists
                                            .flatMap(subjectEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getTeacherUUID())
                                                    // check if classroom uuid exists
                                                    .flatMap(teacherEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getClassroomUUID())
                                                            // check if lecture Type uuid exists
                                                            .flatMap(classroomEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getLectureTypeUUID())
                                                                    // check if lecture Delivery Mode uuid exists
                                                                    .flatMap(lectureTypeEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getLectureDeliveryModeUUID())
                                                                            .flatMap(lectureDeliveryModeEntity -> {
                                                                                        // section, enrollment and student Group uuids are given with timetable
                                                                                        if (updatedTimetableCreationEntity.getSectionUUID() != null && updatedTimetableCreationEntity.getEnrollmentUUID() != null && updatedTimetableCreationEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section, Student Group and Enrollment All cannot be entered at the same time.");
                                                                                        }
                                                                                        // section and enrollment uuids are given with timetable
                                                                                        else if (updatedTimetableCreationEntity.getEnrollmentUUID() != null && updatedTimetableCreationEntity.getSectionUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Section cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and enrollment uuids are given with timetable
                                                                                        else if (updatedTimetableCreationEntity.getEnrollmentUUID() != null && updatedTimetableCreationEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Enrollment and Student Group cannot be entered at the same time.");
                                                                                        }
                                                                                        // student Group and section uuids are given with timetable
                                                                                        else if (updatedTimetableCreationEntity.getSectionUUID() != null && updatedTimetableCreationEntity.getStudentGroupUUID() != null) {
                                                                                            return responseInfoMsg("Section and Student Group cannot be entered at the same time.");
                                                                                        } else if (updatedTimetableCreationEntity.getSectionUUID() != null) {
                                                                                            //Check if the entered Section is Already Allocated in the Given Academic Session, Start time and end time in the same day
                                                                                            return sectionRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getSectionUUID())
                                                                                                    .flatMap(section -> timetableCreationRepository.findBySectionAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                    (timetableCreationUUID, updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID(), updatedTimetableCreationEntity.getSectionUUID())
                                                                                                            .flatMap(checkSectionMsg -> responseInfoMsg("The Entered Section is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                            //check if the section is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                            (timetableCreationUUID, updatedTimetableCreationEntity.getSectionUUID(), updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getSubjectUUID(), updatedTimetableCreationEntity.getClassroomUUID(), updatedTimetableCreationEntity.getTeacherUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID())
                                                                                                                    // save timetable record
                                                                                                                    .flatMap(checkSection -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                            .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                            .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ))
                                                                                                            ))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Section Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Section Does not Exist.Please Contact Developer."));

                                                                                        }

                                                                                        // if enrollment uuid is given with timetable
                                                                                        else if (updatedTimetableCreationEntity.getEnrollmentUUID() != null) {
                                                                                            //Check if the entered Enrollment is Already Allocated in the Given Start time and end time in the same day
                                                                                            return enrollmentRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getEnrollmentUUID())
                                                                                                    .flatMap(enrollment -> {
                                                                                                                //check if enrollment subject and entered subject matches or not
                                                                                                                return subjectOfferedRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getAcademicSessionUUID(), enrollment.getSubjectOfferedUUID())
                                                                                                                        .flatMap(subjectOffered -> timetableCreationRepository.findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                                        (timetableCreationUUID, updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID(), updatedTimetableCreationEntity.getEnrollmentUUID())
                                                                                                                                .flatMap(checkEnrollmentMsg -> responseInfoMsg("The Entered Enrollment is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                                                //check if the enrollment is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                                                .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                                                (timetableCreationUUID, updatedTimetableCreationEntity.getEnrollmentUUID(), updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getSubjectUUID(), updatedTimetableCreationEntity.getClassroomUUID(), updatedTimetableCreationEntity.getTeacherUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID())
                                                                                                                                        // save timetable record
                                                                                                                                        .flatMap(checkEnrollment -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                                                .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                                                .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                                                .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                                        ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                                                .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                                                .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                                                .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                                        ))
                                                                                                                                ))).switchIfEmpty(responseInfoMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session."))
                                                                                                                        .onErrorResume(ex -> responseErrorMsg("The Entered Enrollment Does not Exist Against for the Given Subject and Academic Session.Please Contact Developer."));
                                                                                                            }
                                                                                                    ).switchIfEmpty(responseInfoMsg("Enrollment Does not Exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Enrollment Does not Exist.Please Contact Developer."));
                                                                                        }

                                                                                        // if student Group uuid is given with timetable
                                                                                        else if (updatedTimetableCreationEntity.getStudentGroupUUID() != null) {
                                                                                            //Check if the entered student Group is Already Allocated in the Given Start time and end time in the same day
                                                                                            return studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedTimetableCreationEntity.getStudentGroupUUID())
                                                                                                    //check if Student group is already assigned for given day between start time and end time
                                                                                                    .flatMap(studentGroup -> timetableCreationRepository.findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot
                                                                                                                    (timetableCreationUUID, updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID(), updatedTimetableCreationEntity.getStudentGroupUUID())
                                                                                                            .flatMap(checkStudentGroupMsg -> responseInfoMsg("The Entered Student Group is Already Assigned in the Given Academic Session, Day between the entered start and end date"))
                                                                                                            //check if the Student Group is not same but the start time end time subject, classroom and teacher is same than overlap the entered record
                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIsAndTimetableUUIDIsNot
                                                                                                                            (timetableCreationUUID, updatedTimetableCreationEntity.getStudentGroupUUID(), updatedTimetableCreationEntity.getDayUUID(), updatedTimetableCreationEntity.getSubjectUUID(), updatedTimetableCreationEntity.getClassroomUUID(), updatedTimetableCreationEntity.getTeacherUUID(), updatedTimetableCreationEntity.getStartTime(), updatedTimetableCreationEntity.getEndTime(), updatedTimetableCreationEntity.getAcademicSessionUUID())
                                                                                                                    // save timetable record
                                                                                                                    .flatMap(checkStudentGroup -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                            .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ).switchIfEmpty(Mono.defer(() -> timetableCreationRepository.save(previousTimetableCreationEntity)
                                                                                                                            .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                                                                                                            .flatMap(saveTimetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", saveTimetableCreationEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                                                                                    ))
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


    @AuthHasPermission(value = "academic_api_v1_timetable-creations_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return timetableCreationRepository.findByUuidAndDeletedAtIsNull(timetableCreationUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TimetableCreationEntity updatedTimetableCreationEntity = TimetableCreationEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
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

                                return timetableCreationRepository.save(previousEntity)
                                        .then(timetableCreationRepository.save(updatedTimetableCreationEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-creations_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID timetableCreationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return timetableCreationRepository.findByUuidAndDeletedAtIsNull(timetableCreationUUID)
                .flatMap(timetableCreationEntity -> {

                    timetableCreationEntity.setDeletedBy(UUID.fromString(userId));
                    timetableCreationEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));

                    return timetableCreationRepository.save(timetableCreationEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

//    --------------------------------- Custom Functions ----------------------------------------

//    //check If Enrollment And Section Exists
//    public Mono<ServerResponse> storeEnrollmentAndSection(TimetableCreationEntity timetableCreationEntity) {
//        //check if section is Already Assigned in given time in the given day
//        return timetableRepository.findFirstBySectionUUIDAndEnrollmentUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNull(timetableCreationEntity.getSectionUUID(), timetableCreationEntity.getEnrollmentUUID(), timetableCreationEntity.getDayUUID(), timetableCreationEntity.getStartTime(), timetableCreationEntity.getEndTime())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Section and Enrollment is already allocated between the Given Time Against this Day!"))
//                //check if Enrollment exists against the entered Academic Session
//                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(timetableCreationEntity.getAcademicSessionUUID(), timetableCreationEntity.getEnrollmentUUID())
//                        //check if section exist in Section table
//                        .flatMap(enrollment -> sectionRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getSectionUUID())
//                                //save Timetable Record
//                                .flatMap(section -> timetableRepository.save(timetableCreationEntity)
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Stored Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Section record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Section record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
//                ));
//    }
//
//    //check If Enrollment And Student Group Exists
//    public Mono<ServerResponse> storeEnrollmentAndStudentGroup(TimetableCreationEntity timetableCreationEntity) {
//        //check if student Group is Already Assigned in given time in the given day
//        return timetableRepository.findFirstByStudentGroupUUIDAndEnrollmentUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID(), timetableCreationEntity.getEnrollmentUUID(), timetableCreationEntity.getDayUUID(), timetableCreationEntity.getStartTime(), timetableCreationEntity.getEndTime())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group and Enrollment is already allocated between the Given Time Against this Day!"))
//                //check if Enrollment exists against the entered Academic Session
//                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(timetableCreationEntity.getAcademicSessionUUID(), timetableCreationEntity.getEnrollmentUUID())
//                        //check if student Group exists
//                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID())
//                                .flatMap(studentGroupEntity -> timetableRepository.save(timetableCreationEntity)
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Stored Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
//                ));
//    }
//
//    //check If Section And Student Group Exists
//    public Mono<ServerResponse> storeSectionAndStudentGroup(TimetableCreationEntity timetableCreationEntity) {
//        //check if student Group and Section are Already Assigned in given time in the given day
//        return timetableRepository.findFirstByStudentGroupUUIDAndSectionUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID(), timetableCreationEntity.getSectionUUID(), timetableCreationEntity.getDayUUID(), timetableCreationEntity.getStartTime(), timetableCreationEntity.getEndTime())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group And Section are already allocated between the Given Time Against this Day!"))
//                //check if Section exists
//                .switchIfEmpty(Mono.defer(() -> sectionRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getSectionUUID())
//                        //check if Student group exists
//                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID())
//                                //save timetable Record
//                                .flatMap(studentGroupEntity -> timetableRepository.save(timetableCreationEntity)
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Stored Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Section record does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Section record does not Exist.Please Contact Developer."))
//                ));
//    }
//
////    //check If Enrollment, Section And Student Group Exists
////    public Mono<ServerResponse> storeEnrollmentAndSectionAndStudentGroup(TimetableCreationEntity timetableCreationEntity) {
////        //check if Student Group and Section is assigned to given time against the given day
////        return timetableRepository.findFirstByStudentGroupUUIDAndSectionUUIDAndEnrollmentUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID(), timetableCreationEntity.getSectionUUID(), timetableCreationEntity.getEnrollmentUUID(), timetableCreationEntity.getDayUUID(), timetableCreationEntity.getStartTime(), timetableCreationEntity.getEndTime())
////                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group, Section and Enrollment are already allocated between the Given Time Against this Day!"))
////                //check if Enrollment Record exists against the Entered Timetable Record
////                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(timetableCreationEntity.getAcademicSessionUUID(), timetableCreationEntity.getEnrollmentUUID())
////                        //check if student Group Record exists in student group table
////                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getStudentGroupUUID())
////                                //check if section Record exists in sections table
////                                .flatMap(studentGroupEntity -> sectionRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getSectionUUID())
////                                        //save Timetable record
////                                        .flatMap(sectionEntity -> timetableRepository.save(timetableCreationEntity)
////                                                .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity1))
////                                                .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
////                                                .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
////                                        ).switchIfEmpty(responseInfoMsg("Section record does not exist."))
////                                        .onErrorResume(ex -> responseErrorMsg("Section record does not exist.Please Contact Developer."))
////                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
////                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
////                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
////                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
////                ));
////    }
//
//
//    //check If Enrollment And Section Exists
//    public Mono<ServerResponse> updateEnrollmentAndSection(TimetableCreationEntity updatedTimeTableEntity, TimetableCreationEntity previousEntity) {
//        //check if section is Already Assigned in given time in the given day
//        return timetableRepository.findFirstBySectionUUIDAndEnrollmentUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNullAndUuidIsNot(updatedTimeTableEntity.getSectionUUID(), updatedTimeTableEntity.getEnrollmentUUID(), updatedTimeTableEntity.getDayUUID(), updatedTimeTableEntity.getStartTime(), updatedTimeTableEntity.getEndTime(), updatedTimeTableEntity.getUuid())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Section and Enrollment is already allocated between the Given Time Against this Day!"))
//                //check if Enrollment exists against the entered Academic Session
//                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedTimeTableEntity.getAcademicSessionUUID(), updatedTimeTableEntity.getEnrollmentUUID())
//                        //check if section exist in Section table
//                        .flatMap(enrollment -> sectionRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getSectionUUID())
//                                //save Timetable Record
//                                .flatMap(section -> timetableRepository.save(previousEntity)
//                                        .then(timetableRepository.save(updatedTimeTableEntity))
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Section record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Section record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
//                ));
//    }
//
//    //check If Enrollment And Student Group Exists
//    public Mono<ServerResponse> updateEnrollmentAndStudentGroup(TimetableCreationEntity updatedTimeTableEntity, TimetableCreationEntity previousEntity) {
//        //check if student Group is Already Assigned in given time in the given day
//        return timetableRepository.findFirstByStudentGroupUUIDAndEnrollmentUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNullAndUuidIsNot(updatedTimeTableEntity.getStudentGroupUUID(), updatedTimeTableEntity.getEnrollmentUUID(), updatedTimeTableEntity.getDayUUID(), updatedTimeTableEntity.getStartTime(), updatedTimeTableEntity.getEndTime(), updatedTimeTableEntity.getUuid())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group is already allocated between the Given Time Against this Day!"))
//                //check if Enrollment exists against the entered Academic Session
//                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedTimeTableEntity.getAcademicSessionUUID(), updatedTimeTableEntity.getEnrollmentUUID())
//                        //check if student Group exists
//                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getStudentGroupUUID())
//                                .flatMap(studentGroupEntity -> timetableRepository.save(previousEntity)
//                                        .then(timetableRepository.save(updatedTimeTableEntity))
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
//                ));
//    }
//
//    //check If Section And Student Group Exists
//    public Mono<ServerResponse> updateSectionAndStudentGroup(TimetableCreationEntity updatedTimeTableEntity, TimetableCreationEntity previousEntity) {
//        //check if student Group and Section are Already Assigned in given time in the given day
//        return timetableRepository.findFirstByStudentGroupUUIDAndSectionUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNullAndUuidIsNot(updatedTimeTableEntity.getStudentGroupUUID(), updatedTimeTableEntity.getSectionUUID(), updatedTimeTableEntity.getDayUUID(), updatedTimeTableEntity.getStartTime(), updatedTimeTableEntity.getEndTime(), updatedTimeTableEntity.getUuid())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group And Section are already allocated between the Given Time Against this Day!"))
//                //check if Section exists
//                .switchIfEmpty(Mono.defer(() -> sectionRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getSectionUUID())
//                        //check if Student group exists
//                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getStudentGroupUUID())
//                                //save timetable Record
//                                .flatMap(studentGroupEntity -> timetableRepository.save(previousEntity)
//                                        .then(timetableRepository.save(updatedTimeTableEntity))
//                                        .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity1))
//                                        .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Section record does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Section record does not Exist.Please Contact Developer."))
//                ));
//    }
//
//    //check If Enrollment, Section And Student Group Exists
//    public Mono<ServerResponse> updateEnrollmentAndSectionAndStudentGroup(TimetableCreationEntity updatedTimeTableEntity, TimetableCreationEntity previousEntity) {
//        //check if Student Group and Section is assigned to given time against the given day
//        return timetableRepository.findFirstByStudentGroupUUIDAndSectionUUIDAndDayUUIDAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedAtIsNullAndUuidIsNot(updatedTimeTableEntity.getStudentGroupUUID(), updatedTimeTableEntity.getSectionUUID(), updatedTimeTableEntity.getDayUUID(), updatedTimeTableEntity.getStartTime(), updatedTimeTableEntity.getEndTime(), updatedTimeTableEntity.getUuid())
//                .flatMap(checkSectionMsg -> responseInfoMsg("The Student Group And Section are already allocated between the Given Time Against this Day!"))
//                //check if Enrollment Record exists against the Entered Timetable Record
//                .switchIfEmpty(Mono.defer(() -> enrollmentRepository.findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(updatedTimeTableEntity.getAcademicSessionUUID(), updatedTimeTableEntity.getEnrollmentUUID())
//                        //check if student Group Record exists in student group table
//                        .flatMap(enrollment -> studentGroupRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getStudentGroupUUID())
//                                //check if section Record exists in sections table
//                                .flatMap(studentGroupEntity -> sectionRepository.findByUuidAndDeletedAtIsNull(updatedTimeTableEntity.getSectionUUID())
//                                        //save Timetable record
//                                        .flatMap(sectionEntity -> timetableRepository.save(previousEntity)
//                                                .then(timetableRepository.save(updatedTimeTableEntity))
//                                                .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity1))
//                                                .switchIfEmpty(responseInfoMsg("Unable to Update record.There is something wrong please try again."))
//                                                .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
//                                        ).switchIfEmpty(responseInfoMsg("Section record does not exist."))
//                                        .onErrorResume(ex -> responseErrorMsg("Section record does not exist.Please Contact Developer."))
//                                ).switchIfEmpty(responseInfoMsg("Student Group record does not exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Student Group record does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment Against the Entered Academic Session Does not Exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment Against the Entered Academic Session Does not Exist.Please Contact Developer."))
//                ));
//    }

    /**
     * Old Functions
     **/

//    public Mono<ServerResponse> storeWithEnrollment(TimetableCreationEntity timetableCreationEntity, AcademicSessionEntity academicSessionEntity) {
//
//        // checks if enrollment uuid exist
//        return enrollmentRepository.findByUuidAndDeletedAtIsNull(timetableCreationEntity.getEnrollmentUUID())
//                // checks if subject offered uuid exists
//                .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                        //checks course subject uuid exists
//                        .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                //checks subject uuid exists
//                                .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                        //checks section uuid exists
//                                        .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSemesterUUID())
//                                                .flatMap(semesterEntity -> {
//
//                                                    timetableCreationEntity.setName(academicSessionEntity.getName() + "-" + semesterEntity.getName());
//
//                                                    return timetableRepository.save(timetableCreationEntity)
//                                                            .flatMap(timetableCreationEntity1 -> responseSuccessMsg("Record Stored Successfully", timetableCreationEntity1))
//                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
//                                                })
//                                        ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
//                                        .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
//                        ).switchIfEmpty(responseInfoMsg("Subject Offered record does not exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Subject Offered record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Enrollment record does not exist. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> storeWithSection(TimetableCreationEntity entity, AcademicSessionEntity academicSessionEntity) {
//
//        // checks if section uuid exists
//        return sectionRepository.findByUuidAndDeletedAtIsNull(entity.getSectionUUID())
//                // checks if enrollment uuid exists
//                .flatMap(sectionEntity -> enrollmentRepository.findByUuidAndDeletedAtIsNull(entity.getEnrollmentUUID())
//                        //checks subject-offered uuid exists
//                        .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                                //checks course subject uuid exists
//                                .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                        //checks subject uuid exists
//                                        .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                                //checks section uuid exists
//                                                .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSemesterUUID())
//                                                        .flatMap(semesterEntity -> {
//                                                            entity.setName(academicSessionEntity.getName() + "-" + semesterEntity.getName());
//                                                            return timetableRepository.save(entity)
//                                                                    .flatMap(timetableCreationEntity -> responseSuccessMsg("Record Stored Successfully", timetableCreationEntity))
//                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
//                                                        })
//                                                ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
//                                                .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
//                                        ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
//                                        .onErrorResume(ex -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Subject Offered record does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Subject Offered record does not exist. Please contact developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Section record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Section record does not exist. Please contact developer."));
//    }


//    public Mono<ServerResponse> updateWithEnrollment(TimetableCreationEntity entity, TimetableCreationEntity updatedEntity, AcademicSessionEntity academicSessionEntity) {
//
//        // checks if enrollment uuid exist
//        return enrollmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getEnrollmentUUID())
//                // checks if subject offered uuid exists
//                .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                        //checks course-subject uuid exists
//                        .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                //checks subject uuid exists
//                                .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                        //checks section uuid exists
//                                        .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSemesterUUID())
//                                                .flatMap(semesterEntity -> {
//                                                    updatedEntity.setName(academicSessionEntity.getName() + "-" + semesterEntity.getName());
//                                                    return timetableRepository.save(entity)
//                                                            .then(timetableRepository.save(updatedEntity))
//                                                            .flatMap(timetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity))
//                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
//                                                            .onErrorResume(err -> responseInfoMsg("Unable to update record. Please contact developer."));
//                                                })
//                                        ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
//                                        .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
//                        ).switchIfEmpty(responseInfoMsg("Subject Offered record does not exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Subject Offered record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Enrollment record does not exist. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> updateWithSection(TimetableCreationEntity entity, TimetableCreationEntity updatedEntity, AcademicSessionEntity academicSessionEntity) {
//
//        // checks if section uuid exists
//        return sectionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSectionUUID())
//                // checks if enrollment uuid exists
//                .flatMap(sectionEntity -> enrollmentRepository.findByUuidAndDeletedAtIsNull(entity.getEnrollmentUUID())
//                        //checks if subject offered uuid exists
//                        .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                                //checks course-subject uuid exists
//                                .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                        //checks subject uuid exists
//                                        .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                                //checks section uuid exists
//                                                .flatMap(subjectEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSemesterUUID())
//                                                        .flatMap(semesterEntity -> {
//                                                            updatedEntity.setName(academicSessionEntity.getName() + "-" + semesterEntity.getName());
//                                                            return timetableRepository.save(entity)
//                                                                    .then(timetableRepository.save(updatedEntity))
//                                                                    .flatMap(timetableCreationEntity -> responseSuccessMsg("Record Updated Successfully", timetableCreationEntity))
//                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
//                                                                    .onErrorResume(err -> responseInfoMsg("Unable to update record. Please contact developer."));
//                                                        })
//                                                ).switchIfEmpty(responseInfoMsg("Subject record does not exist"))
//                                                .onErrorResume(ex -> responseErrorMsg("Subject record does not exist. Please contact developer."))
//                                        ).switchIfEmpty(responseInfoMsg("Course Subject record does not exist"))
//                                        .onErrorResume(ex -> responseErrorMsg("Course Subject record does not exist. Please contact developer."))
//                                ).switchIfEmpty(responseInfoMsg("Subject Offered record does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Subject Offered record does not exist. Please contact developer."))
//                        ).switchIfEmpty(responseInfoMsg("Enrollment record does not exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Enrollment record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Section record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Section record does not exist. Please contact developer."));
//    }
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
