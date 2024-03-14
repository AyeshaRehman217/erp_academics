package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOfferedDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOfferedEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectOfferedRepository;

import java.util.UUID;

@Repository
public interface SlaveSubjectOfferedRepository extends ReactiveCrudRepository<SlaveSubjectOfferedEntity, Long>, SlaveCustomSubjectOfferedRepository {

    Flux<SlaveSubjectOfferedEntity> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select subject_offered.*,course_subject.obe, " +
            "concat(academic_sessions.name,'|',courses.name,'|',subjects.name) as name\n" +
            "from subject_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid = course_subject.course_uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "where subject_offered.uuid = :uuid " +
            "and academic_sessions.deleted_at is null " +
            "and course_subject.deleted_at is null \n" +
            "and courses.deleted_at is null " +
            "and subjects.deleted_at is null " +
            "and subject_offered.deleted_at is null ")
    Mono<SlaveSubjectOfferedDto> showByUuidAndDeletedAtIsNull(UUID uuid);

    @Query(" select count(*)\n" +
            "from subject_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid = course_subject.course_uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "where academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countAllByDeletedAtIsNull(String name);

    @Query("select count(*)\n" +
            "from subject_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid = course_subject.course_uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') " +
            "and subject_offered.status= :status " +
            "and academic_sessions.deleted_at is null " +
            "and course_subject.deleted_at is null \n" +
            "and courses.deleted_at is null " +
            "and subjects.deleted_at is null " +
            "and subject_offered.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNullAndStatus(String name, Boolean status);

    @Query("select count(*) \n" +
            "from subject_offered\n" +
            "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
            "join students on registrations.student_uuid=students.uuid \n" +
            " where students.uuid = :studentUUID " +
            " and courses.uuid = :courseUUID " +
            " and registrations.academic_session_uuid=subject_offered.academic_session_uuid\n" +
            " and campus_course.deleted_at is null\n" +
            " and course_subject.deleted_at is null\n" +
            " and courses.deleted_at is null\n" +
            " and registrations.deleted_at is null\n" +
            " and subject_offered.deleted_at is null\n" +
            "and students.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null" +
            " and subject_offered.status= :status " +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsWithSubjectOfferedStudentCourseAndStatus(UUID studentUUID, UUID courseUUID, String name, Boolean status);

    @Query("select count(*) \n" +
            "from subject_offered\n" +
            "join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
            "join students on registrations.student_uuid=students.uuid \n" +
            " where students.uuid = :studentUUID " +
            " and courses.uuid = :courseUUID " +
            " and registrations.academic_session_uuid=subject_offered.academic_session_uuid\n" +
            " and campus_course.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsWithSubjectOfferedStudentCourse(UUID studentUUID, UUID courseUUID, String name);

    @Query("select count(*)\n" +
            "from subject_offered \n" +
            " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            " join courses on courses.uuid = course_subject.course_uuid\n" +
            " join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            " where academic_sessions.deleted_at is null\n" +
            " and courses.uuid = :courseUUID " +
            " and subject_offered.status = :status " +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " AND \n" +
            " CASE \n" +
            " WHEN course_subject.obe  \n" +
            " THEN \n" +
            " concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:name,'%')   \n" +
            " ELSE     \n" +
            " concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:name,'%')  " +
            " END ")
    Mono<Long> countRecordsWithSubjectOfferedAgainstCourseAndStatus(UUID courseUUID, String name, Boolean status);

    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " join courses on courses.uuid=course_subject.course_uuid \n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join course_offered on course_offered.campus_course_uuid=campus_course.uuid \n" +
            " where courses.uuid= :courseUUID " +
            " and course_offered.academic_session_uuid=subject_offered.academic_session_uuid \n" +
            "  and campus_course.deleted_at is null \n" +
            "  and course_subject.deleted_at is null \n" +
            "  and courses.deleted_at is null \n" +
            "  and subject_offered.deleted_at is null \n" +
            "  and academic_sessions.deleted_at is null \n" +
            "  and subjects.deleted_at is null \n" +
            "  and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsWithSubjectOfferedAgainstCourse(UUID courseUUID, String name);

    /**
     * Count Subject Offered Against -Academic Session, Campus and Courses With and without Status Filter
     **/
    @Query("select  count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where academic_sessions.uuid = :academicSessionUUID " +
            " and campuses.uuid = :campusUUID " +
            " and courses.uuid = :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null\n" +
            " and subject_offered.status = :status " +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCampusAndCourseWithStatusFilter(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, Boolean status, String name);

    @Query("select  count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where academic_sessions.uuid = :academicSessionUUID " +
            " and campuses.uuid = :campusUUID " +
            " and courses.uuid = :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCampusAndCourseWithoutStatusFilter(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, String name);

    /**
     * Count Subject Offered Against -Academic Session and Campus With and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            "  and campuses.uuid= :campusUUID " +
            "  and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            "  and subject_offered.status = :status " +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCampusWithStatusFilter(UUID academicSessionUUID, UUID campusUUID, Boolean status, String name);

    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            "  and campuses.uuid= :campusUUID " +
            "  and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCampusWithoutStatusFilter(UUID academicSessionUUID, UUID campusUUID, String name);

    /**
     * Count Courses Against -Academic Session and Courses With and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and courses.uuid= :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            "  and subject_offered.status = :status " +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCourseWithStatusFilter(UUID academicSessionUUID, UUID courseUUID, Boolean status, String name);

    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and courses.uuid= :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countRecordsAgainstAcademicSessionCourseWithoutStatusFilter(UUID academicSessionUUID, UUID courseUUID, String name);

    /**
     * Count All records based on Campus and Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "  from subject_offered \n" +
            "  join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            "  join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "  join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "  join courses on course_subject.course_uuid=courses.uuid \n" +
            "  join campus_course on courses.uuid=campus_course.course_uuid \n" +
            "  join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            "  where campuses.uuid= :campusUUID " +
            "  and courses.uuid= :courseUUID " +
            "  and subject_offered.deleted_at is null \n" +
            "  and course_subject.deleted_at is null \n" +
            "  and subjects.deleted_at is null \n" +
            "  and academic_sessions.deleted_at is null \n" +
            "  and courses.deleted_at is null \n" +
            "  and campus_course.deleted_at is null \n" +
            "  and campuses.deleted_at is null \n" +
            "  and subject_offered.status = :status " +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countWithCourseAndCampusWithStatus(UUID campusUUID, UUID courseUUID, String name, Boolean status);


    @Query("select count(*) \n" +
            "  from subject_offered \n" +
            "  join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            "  join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "  join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "  join courses on course_subject.course_uuid=courses.uuid \n" +
            "  join campus_course on courses.uuid=campus_course.course_uuid \n" +
            "  join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            "  where campuses.uuid= :campusUUID " +
            "  and courses.uuid= :courseUUID " +
            "  and subject_offered.deleted_at is null \n" +
            "  and course_subject.deleted_at is null \n" +
            "  and subjects.deleted_at is null \n" +
            "  and academic_sessions.deleted_at is null \n" +
            "  and courses.deleted_at is null \n" +
            "  and campus_course.deleted_at is null \n" +
            "  and campuses.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countWithCourseAndCampus(UUID campusUUID, UUID courseUUID, String name);


    /**
     * Count All records based on Academic Session filter (used in enrollments) --> with and without Status Filter
     **/

    @Query(" select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            " and subject_offered.status = :status \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countAgainstSessionWithStatus(UUID academicSessionUUID, String name, Boolean status);


    @Query(" select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countAgainstSessionWithoutStatus(UUID academicSessionUUID, String name);


    /**
     * Count All records based on Campus filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where campuses.uuid= :campusUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and subject_offered.status = :status \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countCampusWithStatus(UUID campusUUID, String name, Boolean status);


    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join campus_course on courses.uuid=campus_course.course_uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where campuses.uuid= :campusUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countCampusWithoutStatus(UUID campusUUID, String name);


    /**
     * Count All records based on Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where courses.uuid= :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            " and subject_offered.status = :status \n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countCourseWithStatus(UUID courseUUID, String name, Boolean status);

    @Query("select count(*) \n" +
            " from subject_offered \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            " join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where courses.uuid= :courseUUID " +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null\n" +
            " and concat(academic_sessions.name,'|',courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') ")
    Mono<Long> countCourseWithoutStatus(UUID courseUUID, String name);

    @Query("select count(*)\n" +
            "from subject_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            "join courses on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "where academic_sessions.deleted_at is null\n" +
            "and course_subject.obe = :obe \n" +
            "and courses.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "AND \n" +
            "CASE \n" +
            "WHEN course_subject.obe\n" +
            "THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:name,'%') \n" +
            "ELSE concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:name,'%') \n" +
            "END ")
    Mono<Long> countSubjectOfferedWithObeFilter(Boolean obe, String name);

    @Query("select count(*) \n" +
            "from subject_offered\n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid\n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            "join courses on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid\n" +
            "where academic_sessions.deleted_at is null\n" +
            "and courses.uuid =:courseUUID \n" +
            "and course_subject.obe =:obe \n" +
            "and courses.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            "AND\n" +
            "CASE \n" +
            "WHEN course_subject.obe\n" +
            " THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:name,'%') \n" +
            " ELSE \n" +
            "concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:name,'%') \n" +
            "END")
    Mono<Long> countSubjectOfferedAgainstCourseAndOBE(UUID courseUUID, Boolean obe, String name);

    @Query("select count(*)\n" +
            " from subject_offered \n" +
            " join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            " join course_subject on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            " join courses on courses.uuid = course_subject.course_uuid \n" +
            " join course_levels on courses.course_level_uuid = course_levels.uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " where academic_sessions.deleted_at is null \n" +
            "and courses.uuid= :courseUUID \n" +
            "and subject_offered.status = :status \n" +
            "and course_subject.obe = :obe \n" +
            "and courses.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " AND \n" +
            " CASE \n" +
            " WHEN course_subject.obe \n" +
            " THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:name,'%') \n" +
            " ELSE\n" +
            " concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:name,'%') \n" +
            " END ")
    Mono<Long> countSubjectOfferedAgainstCourseOBEAndStatus(UUID courseUUID, Boolean obe, Boolean status, String name);

    @Query("select count(*) \n" +
            "from subject_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = subject_offered.academic_session_uuid \n" +
            "join course_subject on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            "join courses on courses.uuid = course_subject.course_uuid \n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid \n" +
            "join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            "where academic_sessions.deleted_at is null \n" +
            "and subject_offered.status =:status \n" +
            "and course_subject.obe =:obe \n" +
            "and courses.deleted_at is null\n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "AND \n" +
            "CASE \n" +
            "WHEN course_subject.obe \n" +
            "THEN concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:name,'%') \n" +
            "ELSE \n" +
            "concat(academic_sessions.name,'|', course_levels.short_name, '|',courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:name,'%') \n" +
            "END")
    Mono<Long> countSubjectOfferedAgainstOBEAndStatus(Boolean obe, Boolean status, String name);
}
