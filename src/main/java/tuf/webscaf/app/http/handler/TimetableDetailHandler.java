//package tuf.webscaf.app.http.handler;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.master.entity.TimetableDetailEntity;
//import tuf.webscaf.app.dbContext.master.repositry.*;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveTimetableDetailEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveTimetableDetailRepository;
//import tuf.webscaf.app.service.ApiCallService;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Tag(name = "timetableDetailHandler")
//@Component
//public class TimetableDetailHandler {
//
//    @Value("${server.zone}")
//    private String zone;
//
//    @Autowired
//    CustomResponse appresponse;
//
////    @Autowired
////    TimetableDetailRepository timetableDetailRepository;
//
//    @Autowired
//    SlaveTimetableDetailRepository slaveTimetableDetailRepository;
//
//    @Autowired
//    CampusRepository campusRepository;
//
//    @Autowired
//    ClassroomRepository classroomRepository;
//
//    @Autowired
//    TeacherRepository teacherRepository;
//
//    @Autowired
//    SubjectRepository subjectRepository;
//
//    @Autowired
//    StudentGroupRepository studentGroupRepository;
//
//    @Autowired
//    LectureTypeRepository lectureTypeRepository;
//
//    @Autowired
//    SemesterRepository semesterRepository;
//
//    @Autowired
//    AttendanceRepository attendanceRepository;
//
//    @Autowired
//    SectionRepository sectionRepository;
//
//    @Autowired
//    CourseRepository courseRepository;
//
//    @Autowired
//    EnrollmentRepository enrollmentRepository;
//
//    @Autowired
//    AcademicSessionRepository academicSessionRepository;
//
//    @Autowired
//    CampusCourseRepository campusCourseRepository;
//
//    @Autowired
//    LectureDeliveryModeRepository lectureDeliveryModeRepository;
//
//    @Autowired
//    SubjectOfferedRepository subjectOfferedRepository;
//
//    @Autowired
//    CourseSubjectRepository courseSubjectRepository;
//
//    @Autowired
//    TimetableCreationRepository timetableRepository;
//
//    @Autowired
//    ApiCallService apiCallService;
//
//    @Value("${server.erp_config_module.uri}")
//    private String configUri;
//
//
//    public Mono<ServerResponse> index(ServerRequest serverRequest) {
//
//        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
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
//        Optional<String> status = serverRequest.queryParam("status");
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//        if (status.isPresent()) {
//            Flux<SlaveTimetableDetailEntity> slaveTimetableDetailFlux = slaveTimetableDetailRepository
//                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status.get()));
//
//            return slaveTimetableDetailFlux
//                    .collectList()
//                    .flatMap(timetableDetailEntity -> slaveTimetableDetailRepository
//                            .countByStatusAndDeletedAtIsNull(Boolean.valueOf(status.get()))
//                            .flatMap(count -> {
//                                if (timetableDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        } else {
//            Flux<SlaveTimetableDetailEntity> slaveTimetableDetailFlux = slaveTimetableDetailRepository
//                    .findAllByDeletedAtIsNull(pageable);
//
//            return slaveTimetableDetailFlux
//                    .collectList()
//                    .flatMap(timetableDetailEntity -> slaveTimetableDetailRepository.countByDeletedAtIsNull()
//                            .flatMap(count -> {
//                                if (timetableDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
//    }
//
//    public Mono<ServerResponse> show(ServerRequest serverRequest) {
//        UUID timetableDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//
//        return slaveTimetableDetailRepository.findByUuidAndDeletedAtIsNull(timetableDetailUUID)
//                .flatMap(timetableDetailEntity -> responseSuccessMsg("Record Fetched Successfully", timetableDetailEntity))
//                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
//    }
//
//
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
//                    TimetableDetailEntity entity = TimetableDetailEntity.builder()
//                            .uuid(UUID.randomUUID())
//                            .timetableUUID(UUID.fromString(value.getFirst("timetableUUID").trim()))
//                            .startTime(LocalDateTime.parse(value.getFirst("startTime"),
//                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                            .endTime(LocalDateTime.parse(value.getFirst("endTime"),
//                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                            .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
//                            .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
//                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
//                            .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
//                            .calendarDateUUID(UUID.fromString(value.getFirst("calendarDateUUID").trim()))
//                            .status(Boolean.valueOf(value.getFirst("status")))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//
//                    // check if timetable uuid exist
//                    return timetableRepository.findByUuidAndDeletedAtIsNull(entity.getTimetableUUID())
//                            // check if lecture type uuid exist
//                            .flatMap(timetableEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(entity.getLectureTypeUUID())
//                                    //checks classroom uuid exists
//                                    .flatMap(lectureTypeEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(entity.getClassroomUUID())
//                                            //checks teacher uuid exists
//                                            .flatMap(classroomEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherUUID())
//                                                    //checks lecture delivery mode uuid exists
//                                                    .flatMap(teacherEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(entity.getLectureDeliveryModeUUID())
//                                                            // check if calendar date uuid exists
//                                                            .flatMap(lectureDeliveryModeEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/calendar-dates/show/", entity.getCalendarDateUUID())
//                                                                    .flatMap(calendarDateJson -> apiCallService.getUUID(calendarDateJson)
//                                                                            .flatMap(calendarDateUUID -> {
//
//                                                                                // if section uuid is given in timetable
//                                                                                if (timetableEntity.getSectionUUID() != null) {
//                                                                                    return campusCourseRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getCampusCourseUUID())
//                                                                                            .flatMap(campusCourseEntity -> sectionRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getSectionUUID())
//                                                                                                    .flatMap(sectionEntity -> enrollmentRepository.findByUuidAndDeletedAtIsNull(sectionEntity.getEnrollmentUUID())
//                                                                                                            //checks subject offered uuid exists
//                                                                                                            .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                                                                                                                    //checks course-subject uuid exists
//                                                                                                                    .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                                                                                                            //checks subject uuid exists
//                                                                                                                            .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                                                                                                                    //checks section uuid exists
//                                                                                                                                    .flatMap(subjectEntity -> timetableDetailRepository.checkClassroomIsOccupied(campusCourseEntity.getCampusUUID(),
//                                                                                                                                                    entity.getClassroomUUID(), subjectEntity.getUuid(), entity.getTeacherUUID(), entity.getStartTime(), entity.getEndTime(), entity.getCalendarDateUUID())
//                                                                                                                                            .flatMap(classroomAlreadyOccupied -> responseInfoMsg("This Classroom has been already Occupied At This Time"))
//                                                                                                                                            .switchIfEmpty(Mono.defer(() -> {
//                                                                                                                                                if (!(campusCourseEntity.getCampusUUID().equals(classroomEntity.getCampusUUID()))) {
//                                                                                                                                                    return responseInfoMsg("The Selected Classroom Does not Exist in this Campus");
//
//                                                                                                                                                } else {
//                                                                                                                                                    // checks if section is already occupied at the given time
//                                                                                                                                                    return timetableDetailRepository.findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNull(entity.getTimetableUUID(),
//                                                                                                                                                                    entity.getCalendarDateUUID(), entity.getStartTime(), entity.getEndTime())
//                                                                                                                                                            .flatMap(sectionAlreadyExists -> responseInfoMsg("Section Cannot be in Two Classes at the same time"))
//                                                                                                                                                            // check if teacher is already occupied at the given time
//                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.findFirstByTeacherUUIDAndClassroomUUIDIsNotAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNull(entity.getTeacherUUID(),
//                                                                                                                                                                            entity.getClassroomUUID(), entity.getCalendarDateUUID(), entity.getStartTime(), entity.getEndTime())
//                                                                                                                                                                    .flatMap(teacherAlreadyExists -> responseInfoMsg("Teacher Cannot be in Two Classes at the same time"))))
//                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.save(entity)
//                                                                                                                                                                    .flatMap(timetableDetailEntity -> responseSuccessMsg("Record Stored Successfully", timetableDetailEntity))
//                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                                                                                                                                            ));
//                                                                                                                                                }
//                                                                                                                                            }))
//
//                                                                                                                                    ))
//                                                                                                                    ))
//                                                                                                    ));
//
//                                                                                } else {
//                                                                                    return campusCourseRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getCampusCourseUUID())
//                                                                                            .flatMap(campusCourseEntity -> {
//                                                                                                if (!(campusCourseEntity.getCampusUUID().equals(classroomEntity.getCampusUUID()))) {
//                                                                                                    return responseInfoMsg("The Selected Classroom Does not Exist in this Campus");
//
//                                                                                                } else {
//                                                                                                    // checks if student is already occupied at the given time
//                                                                                                    return timetableDetailRepository.findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNull(entity.getTimetableUUID(),
//                                                                                                                    entity.getCalendarDateUUID(), entity.getStartTime(), entity.getEndTime())
//                                                                                                            .flatMap(sectionAlreadyExists -> responseInfoMsg("Student Cannot be in Two Classes at the same time"))
//                                                                                                            .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.save(entity)
//                                                                                                                    .flatMap(timetableDetailEntity -> responseSuccessMsg("Record Stored Successfully", timetableDetailEntity))
//                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                                                                                            ));
//                                                                                                }
//                                                                                            });
//                                                                                }
//
//                                                                            }).switchIfEmpty(responseInfoMsg("Calendar Date record does not exist"))
//                                                                            .onErrorResume(ex -> responseErrorMsg("Calendar Date record does not exist. Please contact developer."))
//                                                                    )).switchIfEmpty(responseInfoMsg("Lecture delivery mode record does not exist"))
//                                                            .onErrorResume(ex -> responseErrorMsg("Lecture delivery mode record does not exist. Please contact developer."))
//                                                    ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
//                                                    .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."))
//                                            ).switchIfEmpty(responseInfoMsg("Classroom record does not exist"))
//                                            .onErrorResume(ex -> responseErrorMsg("Classroom record does not exist. Please contact developer."))
//                                    ).switchIfEmpty(responseInfoMsg("Lecture Type record does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Lecture Type record does not exist. Please contact developer."))
//                            ).switchIfEmpty(responseInfoMsg("Timetable record does not exist"))
//                            .onErrorResume(ex -> responseErrorMsg("Timetable record does not exist. Please contact developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID timetableDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
//                .flatMap(value -> timetableDetailRepository.findByUuidAndDeletedAtIsNull(timetableDetailUUID)
//                        .flatMap(entity -> {
//
//                            TimetableDetailEntity updatedEntity = TimetableDetailEntity.builder()
//                                    .uuid(entity.getUuid())
//                                    .timetableUUID(UUID.fromString(value.getFirst("timetableUUID").trim()))
//                                    .startTime(LocalDateTime.parse(value.getFirst("startTime"),
//                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                                    .endTime(LocalDateTime.parse(value.getFirst("endTime"),
//                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                                    .lectureTypeUUID(UUID.fromString(value.getFirst("lectureTypeUUID").trim()))
//                                    .classroomUUID(UUID.fromString(value.getFirst("classroomUUID").trim()))
//                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
//                                    .lectureDeliveryModeUUID(UUID.fromString(value.getFirst("lectureDeliveryModeUUID").trim()))
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .createdAt(entity.getCreatedAt())
//                                    .createdBy(entity.getCreatedBy())
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .updatedBy(UUID.fromString(userId))
//                                    .build();
//
//                            entity.setDeletedBy(UUID.fromString(userId));
//                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//
//                            // check if timetable uuid exist
//                            return timetableRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTimetableUUID())
//                                    // check lecture type uuid exist
//                                    .flatMap(timetableEntity -> lectureTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getLectureTypeUUID())
//                                            //checks classroom uuid exists
//                                            .flatMap(lectureTypeEntity -> classroomRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getClassroomUUID())
//                                                    //checks teacher uuid exists
//                                                    .flatMap(classroomEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
//                                                            //checks lecture delivery mode uuid exists
//                                                            .flatMap(teacherEntity -> lectureDeliveryModeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getLectureDeliveryModeUUID())
//                                                                    // check calendar UUID exists
//                                                                    .flatMap(lectureDeliveryModeEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/calendar-dates/show/", updatedEntity.getCalendarDateUUID())
//                                                                            .flatMap(calendarDateJson -> apiCallService.getUUID(calendarDateJson)
//                                                                                    .flatMap(calendarDateUUID -> {
//
//                                                                                        // if section uuid is given in timetable
//                                                                                        if (timetableEntity.getSectionUUID() != null) {
//                                                                                            return campusCourseRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getCampusCourseUUID())
//                                                                                                    .flatMap(campusCourseEntity -> sectionRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getSectionUUID())
//                                                                                                            .flatMap(sectionEntity -> enrollmentRepository.findByUuidAndDeletedAtIsNull(sectionEntity.getEnrollmentUUID())
//                                                                                                                    //checks subject offered uuid exists
//                                                                                                                    .flatMap(enrollmentEntity -> subjectOfferedRepository.findByUuidAndDeletedAtIsNull(enrollmentEntity.getSubjectOfferedUUID())
//                                                                                                                            //checks course-subject uuid exists
//                                                                                                                            .flatMap(subjectOfferedEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
//                                                                                                                                    //checks subject uuid exists
//                                                                                                                                    .flatMap(courseSubjectEntity -> subjectRepository.findByUuidAndDeletedAtIsNull(courseSubjectEntity.getSubjectUUID())
//                                                                                                                                            //checks section uuid exists
//                                                                                                                                            .flatMap(subjectEntity -> timetableDetailRepository.checkClassroomIsOccupiedAndUUIDIsNot(campusCourseEntity.getCampusUUID(),
//                                                                                                                                                            updatedEntity.getClassroomUUID(), subjectEntity.getUuid(), updatedEntity.getTeacherUUID(), updatedEntity.getStartTime(), updatedEntity.getEndTime(), updatedEntity.getCalendarDateUUID(), updatedEntity.getUuid())
//                                                                                                                                                    .flatMap(classroomAlreadyOccupied -> responseInfoMsg("This Classroom has been already Occupied At This Time"))
//                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> {
//                                                                                                                                                        if (!(campusCourseEntity.getCampusUUID().equals(classroomEntity.getCampusUUID()))) {
//                                                                                                                                                            return responseInfoMsg("The Selected Classroom Does not Exist in this Campus");
//
//                                                                                                                                                        } else {
//                                                                                                                                                            // checks if section is already occupied at the given time
//                                                                                                                                                            return timetableDetailRepository.findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTimetableUUID(),
//                                                                                                                                                                            updatedEntity.getCalendarDateUUID(), updatedEntity.getStartTime(), updatedEntity.getEndTime(), updatedEntity.getUuid())
//                                                                                                                                                                    .flatMap(sectionAlreadyExists -> responseInfoMsg("Section Cannot be in Two Classes at the same time"))
//                                                                                                                                                                    // check if teacher is already occupied at the given time
//                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.findFirstByTeacherUUIDAndClassroomUUIDIsNotAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(),
//                                                                                                                                                                                    updatedEntity.getClassroomUUID(), updatedEntity.getCalendarDateUUID(), updatedEntity.getStartTime(), updatedEntity.getEndTime(), updatedEntity.getUuid())
//                                                                                                                                                                            .flatMap(teacherAlreadyExists -> responseInfoMsg("Teacher Cannot be in Two Classes at the same time"))))
//                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.save(entity)
//                                                                                                                                                                            .then(timetableDetailRepository.save(updatedEntity))
//                                                                                                                                                                            .flatMap(timetableDetailEntity -> responseSuccessMsg("Record Stored Successfully", timetableDetailEntity))
//                                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                                                                                                                                                    ));
//                                                                                                                                                        }
//                                                                                                                                                    }))
//
//                                                                                                                                            ))
//                                                                                                                            ))
//                                                                                                            ));
//
//                                                                                        } else {
//                                                                                            return campusCourseRepository.findByUuidAndDeletedAtIsNull(timetableEntity.getCampusCourseUUID())
//                                                                                                    .flatMap(campusCourseEntity -> {
//                                                                                                        if (!(campusCourseEntity.getCampusUUID().equals(classroomEntity.getCampusUUID()))) {
//                                                                                                            return responseInfoMsg("The Selected Classroom Does not Exist in this Campus");
//
//                                                                                                        } else {
//                                                                                                            // checks if student is already occupied at the given time
//                                                                                                            return timetableDetailRepository.findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTimetableUUID(),
//                                                                                                                            updatedEntity.getCalendarDateUUID(), updatedEntity.getStartTime(), updatedEntity.getEndTime(), updatedEntity.getUuid())
//                                                                                                                    .flatMap(sectionAlreadyExists -> responseInfoMsg("Student Cannot be in Two Classes at the same time"))
//                                                                                                                    .switchIfEmpty(Mono.defer(() -> timetableDetailRepository.save(entity)
//                                                                                                                            .then(timetableDetailRepository.save(updatedEntity))
//                                                                                                                            .flatMap(timetableDetailEntity -> responseSuccessMsg("Record Stored Successfully", timetableDetailEntity))
//                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
//                                                                                                                    ));
//                                                                                                        }
//                                                                                                    });
//                                                                                        }
//
//                                                                                    }).switchIfEmpty(responseInfoMsg("Calendar Date record does not exist"))
//                                                                                    .onErrorResume(ex -> responseErrorMsg("Calendar Date record does not exist. Please contact developer."))
//                                                                            )).switchIfEmpty(responseInfoMsg("Lecture delivery mode record does not exist"))
//                                                                    .onErrorResume(ex -> responseErrorMsg("Lecture delivery mode record does not exist. Please contact developer."))
//                                                            ).switchIfEmpty(responseInfoMsg("Teacher record does not exist"))
//                                                            .onErrorResume(ex -> responseErrorMsg("Teacher record does not exist. Please contact developer."))
//                                                    ).switchIfEmpty(responseInfoMsg("Classroom record does not exist"))
//                                                    .onErrorResume(ex -> responseErrorMsg("Classroom record does not exist. Please contact developer."))
//                                            ).switchIfEmpty(responseInfoMsg("Lecture Type record does not exist"))
//                                            .onErrorResume(ex -> responseErrorMsg("Lecture Type record does not exist. Please contact developer."))
//                                    ).switchIfEmpty(responseInfoMsg("Timetable record does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Timetable record does not exist. Please contact developer."));
//                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
//                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> status(ServerRequest serverRequest) {
//        UUID timetableDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//
//
//                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
//                    return timetableDetailRepository.findByUuidAndDeletedAtIsNull(timetableDetailUUID)
//                            .flatMap(val -> {
//                                // If status is not Boolean value
//                                if (status != false && status != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same status exist in database.
//                                if (((val.getStatus() ? true : false) == status)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                TimetableDetailEntity entity = TimetableDetailEntity.builder()
//                                        .uuid(val.getUuid())
//                                        .timetableUUID(val.getTimetableUUID())
//                                        .startTime(val.getStartTime())
//                                        .endTime(val.getEndTime())
//                                        .lectureTypeUUID(val.getLectureTypeUUID())
//                                        .classroomUUID(val.getClassroomUUID())
//                                        .teacherUUID(val.getTeacherUUID())
//                                        .lectureDeliveryModeUUID(val.getLectureDeliveryModeUUID())
//                                        .status(status == true ? true : false)
//                                        .createdAt(val.getCreatedAt())
//                                        .createdBy(val.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .build();
//
//                                // update status
//                                val.setDeletedBy(UUID.fromString(userId));
//                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                return timetableDetailRepository.save(val)
//                                        .then(timetableDetailRepository.save(entity))
//                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
//    }
//
//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        UUID timetableDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
//        return timetableDetailRepository.findByUuidAndDeletedAtIsNull(timetableDetailUUID)
//                .flatMap(timetableDetailEntity -> {
//                    timetableDetailEntity.setDeletedBy(UUID.fromString(userId));
//                    timetableDetailEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                    return timetableDetailRepository.save(timetableDetailEntity)
//                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
//                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
//                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
//                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
//    }
//
//
//
//    public Mono<ServerResponse> responseInfoMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.empty()
//
//        );
//    }
//
//
//    public Mono<ServerResponse> responseErrorMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.ERROR,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//
//    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg)
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg)
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseWarningMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.WARNING,
//                        msg)
//        );
//
//
//        return appresponse.set(
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                HttpStatus.UNPROCESSABLE_ENTITY.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//}
