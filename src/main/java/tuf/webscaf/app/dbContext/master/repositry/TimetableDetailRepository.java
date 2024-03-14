//package tuf.webscaf.app.dbContext.master.repositry;
//
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.master.entity.TimetableDetailEntity;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Repository
//public interface TimetableDetailRepository extends ReactiveCrudRepository<TimetableDetailEntity, Long> {
//
//    Mono<TimetableDetailEntity> findByIdAndDeletedAtIsNull(Long id);
//
//    Mono<TimetableDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
//
//    Mono<TimetableDetailEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
//
//    Mono<TimetableDetailEntity> findFirstByLectureTypeUUIDAndDeletedAtIsNull(UUID lectureTypeUUID);
//
//    Mono<TimetableDetailEntity> findFirstByClassroomUUIDAndDeletedAtIsNull(UUID classroomUUID);
//
//    @Query("SELECT * from timetable_details " +
//            " join timetables on timetables.uuid = timetable_details.timetable_uuid " +
//            " join classrooms on classrooms.uuid = timetable_details.classroom_uuid " +
//            " join campus_course on campus_course.uuid= timetables.campus_course_uuid " +
//            " join campuses on campuses.uuid = campus_course.campus_uuid " +
//            " join sections on sections.uuid = timetables.section_uuid " +
//            " join enrollments on enrollments.uuid=sections.enrollment_uuid " +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid " +
//            " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid " +
//            " join subjects on subjects.uuid = course_subject.subject_uuid" +
//            " where campuses.uuid = :campusUUID and classrooms.uuid= :classroomUUID " +
//            " and subjects.uuid !=  :subjectUUID and timetable_details.teacher_uuid !=  :teacherUUID" +
//            " and timetable_details.start_time = :startTime and timetable_details.end_time= :endTime" +
//            " and calendar_date_uuid= :calendar fetch first row only ")
//    Mono<TimetableDetailEntity> checkClassroomIsOccupied
//            (UUID campusUUID, UUID classroomUUID, UUID subjectUUID, UUID teacherUUID, LocalDateTime startTime, LocalDateTime endTime, UUID calendar);
//
//    @Query("SELECT * from timetable_details " +
//            " join timetables on timetables.uuid = timetable_details.timetable_uuid " +
//            " join classrooms on classrooms.uuid = timetable_details.classroom_uuid " +
//            " join campus_course on campus_course.uuid= timetables.campus_course_uuid " +
//            " join campuses on campuses.uuid = campus_course.campus_uuid " +
//            " join sections on sections.uuid = timetables.section_uuid " +
//            " join enrollments on enrollments.uuid=sections.enrollment_uuid " +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid " +
//            " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid " +
//            " join subjects on subjects.uuid = course_subject.subject_uuid" +
//            " where campuses.uuid = :campusUUID and classrooms.uuid= :classroomUUID " +
//            " and subjects.uuid !=  :subjectUUID and timetable_details.teacher_uuid !=  :teacherUUID" +
//            " and timetable_details.start_time = :startTime and timetable_details.end_time= :endTime" +
//            " and calendar_date_uuid= :calendar and timetables.uuid != :uuid fetch first row only ")
//    Mono<TimetableDetailEntity> checkClassroomIsOccupiedAndUUIDIsNot
//            (UUID campusUUID, UUID classroomUUID, UUID subjectUUID, UUID teacherUUID, LocalDateTime startTime, LocalDateTime endTime, UUID calendar,UUID uuid);
//
//    //This Query Will check the Timetable Cannot have Two Entries At the same Time
//    Mono<TimetableDetailEntity> findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNull(UUID timetableUUID, UUID calendarDateUUID, LocalDateTime startTime, LocalDateTime endTime);
//
//    Mono<TimetableDetailEntity> findFirstByTimetableUUIDAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNullAndUuidIsNot(UUID timetableUUID, UUID calendarDateUUID, LocalDateTime startTime, LocalDateTime endTime,UUID uuid);
//
//    //This Query Will check the Teacher Cannot be in Two Classrooms At the same Time
//    Mono<TimetableDetailEntity> findFirstByTeacherUUIDAndClassroomUUIDIsNotAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNull(UUID teacherUUID, UUID classroomUUID, UUID calendarDateUUID, LocalDateTime startTime, LocalDateTime endTime);
//
//    Mono<TimetableDetailEntity> findFirstByTeacherUUIDAndClassroomUUIDIsNotAndCalendarDateUUIDAndStartTimeAndEndTimeAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID classroomUUID, UUID calendarDateUUID, LocalDateTime startTime, LocalDateTime endTime, UUID uuid);
//
//}
