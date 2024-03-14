package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TimetableCreationEntity;
import tuf.webscaf.app.dbContext.master.entity.TimetableRescheduleEntity;
import tuf.webscaf.app.dbContext.master.entity.TimetableRescheduleEntity;

import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface TimetableRescheduleRepository extends ReactiveCrudRepository<TimetableRescheduleEntity, Long> {

    Mono<TimetableRescheduleEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TimetableRescheduleEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Check if Section record Already exists against the entered day , start time and end Time
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            "WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            "AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            "AND timetable_creations.section_uuid = :sectionUUID " +
            "AND timetable_creations.academic_session_uuid =:academicSessionUUID\n" +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findBySectionAlreadyExistAgainstDayAndTimeAndAcademicSession(UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID sectionUUID);

    /**
     * Check if Enrollment record Already exists against the entered day , start time and end Time
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.enrollment_uuid =:enrollmentUUID\n" +
            " AND timetable_creations.academic_session_uuid=:academicSessionUUID\n" +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTime(UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID enrollmentUUID);

    /**
     * Check if Student Group record Already exists against the entered day , start time and end Time
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            "WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            "AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.student_group_uuid = :studentGroupUUID\n" +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID\n" +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTime(UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID studentGroupUUID);


    /**
     * Check if Section record Already exists against the entered day , start time and end Time (Used By Update Function)
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            "WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            "AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.section_uuid = :sectionUUID" +
            " AND timetable_creations.uuid != :timetableUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findBySectionAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot(UUID timetableUUID, UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID sectionUUID);

    /**
     * Check if Enrollment record Already exists against the entered day , start time and end Time (Used By Update Function)
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            "WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            "AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.enrollment_uuid =:enrollmentUUID\n" +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.uuid != :timetableUUID " +
            "AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findByEnrollmentAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot(UUID timetableUUID, UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID enrollmentUUID);

    /**
     * Check if Student Group record Already exists against the entered day , start time and end Time (Used By Update Function)
     **/
    @Query("SELECT timetable_creations.* \n" +
            "FROM timetable_creations\n" +
            "WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            "AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.student_group_uuid = :studentGroupUUID\n" +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID\n" +
            " AND timetable_creations.uuid != :timetableUUID  " +
            "AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findByStudentGroupAlreadyExistAgainstDayAcademicSessionAndTimeAndTimetableIsNot(UUID timetableUUID, UUID day, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID, UUID studentGroupUUID);


    // Check If the Section is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Storing record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.section_uuid != :sectionUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :AcademicSessionUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs(UUID sectionUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID AcademicSessionUUID);

    // Check If the Section is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Updating record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.section_uuid != :sectionUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.uuid != :timetableUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereSectionUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot(UUID timetableUUID, UUID sectionUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID);

    // Check If the Enrollment is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Storing record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.enrollment_uuid != :enrollmentUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIs(UUID enrollmentUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID);

    // Check If the Enrollment is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Updating record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.enrollment_uuid != :enrollmentUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.uuid != :timetableUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereEnrollmentUUIDIsNotSameButSubjectClassroomDayAcademicSessionAndTeacherIsAndTimetableUUIDIsNot(UUID timetableUUID, UUID enrollmentUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID);


    // Check If the Student Group is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Storing record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.student_group_uuid != :studentGroupUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIs(UUID studentGroupUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID);

    // Check If the Student Group is not same occupied for the Entered in Specific Time with same teacher same classroom same subject (When Updating record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.student_group_uuid != :studentGroupUUID " +
            " AND timetable_creations.subject_uuid =:subjectUUID " +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.academic_session_uuid = :academicSessionUUID " +
            " AND timetable_creations.uuid != :timetableUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findWhereStudentGroupUUIDIsNotSameButSubjectClassroomAcademicSessionDayAndTeacherIsAndTimetableUUIDIsNot(UUID timetableUUID,UUID studentGroupUUID, UUID day, UUID subjectUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime, UUID academicSessionUUID);


    // Check If teacher Already Occupied for the given day between entered start time and end time in Classroom (When Storing record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findTeacherAssignedAgainstClassroomInDayBetweenTime(UUID dayUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime);

    // Check If teacher Already Occupied for the given day between entered start time and end time in Classroom (When Updating record)
    @Query("SELECT timetable_creations.* \n" +
            " FROM timetable_creations\n" +
            " WHERE \n" +
            " (timetable_creations.day = :day) \n" +
            " AND ((timetable_creations.start_time <= :startTime AND timetable_creations.end_time > :startTime)\n" +
            " OR \n" +
            " (timetable_creations.end_time >= :endTime AND timetable_creations.start_time < :endTime)) \n" +
            " AND timetable_creations.classroom_uuid = :classroomUUID " +
            " AND timetable_creations.teacher_uuid = :teacherUUID " +
            " AND timetable_creations.uuid != :timetableUUID " +
            " AND timetable_creations.deleted_at is null ")
    Mono<TimetableCreationEntity> findTeacherAssignedAgainstClassroomInDayBetweenTimeAndTimetableUUIDIsNot(UUID timetableUUID, UUID dayUUID, UUID classroomUUID, UUID teacherUUID, LocalTime startTime, LocalTime endTime);
}


