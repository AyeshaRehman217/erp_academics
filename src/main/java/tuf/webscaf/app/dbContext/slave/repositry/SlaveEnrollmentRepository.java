package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveEnrollmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomEnrollmentRepository;

import java.util.UUID;

@Repository
public interface SlaveEnrollmentRepository extends ReactiveCrudRepository<SlaveEnrollmentEntity, Long>, SlaveCustomEnrollmentRepository {
    Flux<SlaveEnrollmentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveEnrollmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveEnrollmentEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveEnrollmentEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Count All Records with and Without Status Filter
     **/
    @Query("select count(*) \n" +
            " from enrollments \n" +
            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
            " join students on students.uuid = enrollments.student_uuid \n" +
            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            " join courses on courses.uuid = course_subject.course_uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " where enrollments.deleted_at is null \n" +
            " and students.deleted_at is null \n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllByDeletedAtIsNull(String key);

    @Query("select count(*) \n" +
            " from enrollments \n" +
            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
            " join students on students.uuid = enrollments.student_uuid \n" +
            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            " join courses on courses.uuid = course_subject.course_uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " where enrollments.deleted_at is null \n" +
            " and enrollments.status = :status " +
            " and students.deleted_at is null \n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllByDeletedAtIsNullAndStatus(String key, Boolean status);

    /**
     * Count All Records with and Without Academic Session Filter
     **/
    @Query("select count(*) \n" +
            " from enrollments \n" +
            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
            " join students on students.uuid = enrollments.student_uuid \n" +
            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            " join courses on courses.uuid = course_subject.course_uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " where enrollments.deleted_at is null \n" +
            " and enrollments.academic_session_uuid = :academicSessionUUID " +
            " and students.deleted_at is null \n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllRecordsWithAcademicSessionAndDeletedAtIsNull(UUID academicSessionUUID, String key);

    @Query("select count(*) \n" +
            " from enrollments \n" +
            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
            " join students on students.uuid = enrollments.student_uuid \n" +
            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
            " join courses on courses.uuid = course_subject.course_uuid \n" +
            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
            " where enrollments.deleted_at is null \n" +
            " and enrollments.status = :status " +
            " and enrollments.academic_session_uuid = :academicSessionUUID " +
            " and students.deleted_at is null \n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllByDeletedAtIsNullAndAcademicSessionAndStatus(UUID academicSessionUUID, Boolean status, String key);

    /**
     * Count All Records with and Without Academic Session & Subject With and Without Status Filter
     **/
    @Query("select count(*) \n" +
            "from enrollments \n" +
            "join students on enrollments.student_uuid=students.uuid \n" +
            "join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid \n" +
            "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "join courses on course_subject.course_uuid=courses.uuid \n" +
            "where subjects.uuid= :subjectUUID " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            "and enrollments.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null\n" +
            "and courses.deleted_at is null \n" +
            "and semesters.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllRecordsWithAcademicSessionAndSubjectWhereDeletedAtIsNull(UUID academicSessionUUID, UUID subjectUUID, String key);

    @Query("select count(*) \n" +
            "from enrollments \n" +
            "join students on enrollments.student_uuid=students.uuid \n" +
            "join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid \n" +
            "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "join courses on course_subject.course_uuid=courses.uuid \n" +
            "where subjects.uuid= :subjectUUID " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            " and enrollments.status = :status " +
            "and enrollments.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and semesters.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllRecordsWithAcademicSessionAndSubjectAndStatusWhereDeletedAtIsNull(UUID academicSessionUUID, UUID subjectUUID, Boolean status, String key);

    /**
     * Count All Records with and Without Academic Session & Subject With and Without Status Filter
     **/
    @Query("select count(*) \n" +
            "from enrollments \n" +
            "join students on enrollments.student_uuid=students.uuid \n" +
            "join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid \n" +
            "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "join courses on course_subject.course_uuid=courses.uuid \n" +
            "where subjects.uuid= :subjectUUID " +
            "and enrollments.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null\n" +
            "and courses.deleted_at is null \n" +
            "and semesters.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllRecordsWithSubjectWhereDeletedAtIsNull(UUID subjectUUID, String key);

    @Query("select count(*) \n" +
            "from enrollments \n" +
            "join students on enrollments.student_uuid=students.uuid \n" +
            "join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            "join subject_offered on enrollments.subject_offered_uuid=subject_offered.uuid \n" +
            "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            "join course_subject on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "join subjects on course_subject.subject_uuid=subjects.uuid \n" +
            "join courses on course_subject.course_uuid=courses.uuid \n" +
            "where subjects.uuid= :subjectUUID " +
            " and enrollments.status = :status " +
            "and enrollments.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and semesters.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countAllRecordsWithSubjectAndStatusWhereDeletedAtIsNull(UUID subjectUUID, Boolean status, String key);


    /**
     * Count All Records with and Without Academic Session & Course Subject Filter
     **/
//    @Query("select count(*) \n" +
//            " from enrollments \n" +
//            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//            " join students on students.uuid = enrollments.student_uuid \n" +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//            " join courses on courses.uuid = course_subject.course_uuid \n" +
//            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
//            " where enrollments.deleted_at is null \n" +
//            " and enrollments.academic_session_uuid = :academicSessionUUID " +
//            " and course_subject.uuid = :courseSubjectUUID " +
//            " and students.deleted_at is null \n" +
//            " and subject_offered.deleted_at is null\n" +
//            " and course_subject.deleted_at is null \n" +
//            " and courses.deleted_at is null \n" +
//            " and subjects.deleted_at is null \n" +
//            " and semesters.deleted_at is null \n" +
//            " and academic_sessions.deleted_at is null \n" +
//            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//            " ILIKE concat('%',:key,'%') ")
//    Mono<Long> countAllRecordsWithSessionAndCourseSubjectFilter(UUID academicSessionUUID, UUID courseSubjectUUID, String key);

//    @Query("select count(*) \n" +
//            " from enrollments \n" +
//            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//            " join students on students.uuid = enrollments.student_uuid \n" +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//            " join courses on courses.uuid = course_subject.course_uuid \n" +
//            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
//            " where enrollments.deleted_at is null \n" +
//            " and enrollments.status = :status " +
//            " and enrollments.academic_session_uuid = :academicSessionUUID " +
//            " and course_subject.uuid = :courseSubjectUUID " +
//            " and students.deleted_at is null \n" +
//            " and subject_offered.deleted_at is null\n" +
//            " and course_subject.deleted_at is null \n" +
//            " and courses.deleted_at is null \n" +
//            " and subjects.deleted_at is null \n" +
//            " and semesters.deleted_at is null \n" +
//            " and academic_sessions.deleted_at is null \n" +
//            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//            " ILIKE concat('%',:key,'%') ")
//    Mono<Long> countAllRecordsWithSessionCourseSubjectAndStatusFilter(UUID academicSessionUUID, UUID courseSubjectUUID, Boolean status, String key);

    /**
     * Count All Records Against Course Subject with and Without Status Filter
     **/
//    @Query("select count(*) \n" +
//            " from enrollments \n" +
//            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//            " join students on students.uuid = enrollments.student_uuid \n" +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//            " join courses on courses.uuid = course_subject.course_uuid \n" +
//            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
//            " where enrollments.deleted_at is null \n" +
//            " and course_subject.uuid = :courseSubjectUUID " +
//            " and students.deleted_at is null \n" +
//            " and subject_offered.deleted_at is null\n" +
//            " and course_subject.deleted_at is null \n" +
//            " and courses.deleted_at is null \n" +
//            " and subjects.deleted_at is null \n" +
//            " and semesters.deleted_at is null \n" +
//            " and academic_sessions.deleted_at is null \n" +
//            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//            " ILIKE concat('%',:key,'%') ")
//    Mono<Long> countAllRecordsWithCourseSubjectFilter(UUID courseSubjectUUID, String key);

//    @Query("select count(*) \n" +
//            " from enrollments \n" +
//            " join academic_sessions  on academic_sessions.uuid = enrollments.academic_session_uuid \n" +
//            " join semesters  on semesters.uuid = enrollments.semester_uuid \n" +
//            " join students on students.uuid = enrollments.student_uuid \n" +
//            " join subject_offered on subject_offered.uuid = enrollments.subject_offered_uuid \n" +
//            " join course_subject  on course_subject.uuid = subject_offered.course_subject_uuid \n" +
//            " join courses on courses.uuid = course_subject.course_uuid \n" +
//            " join subjects on subjects.uuid = course_subject.subject_uuid \n" +
//            " where enrollments.deleted_at is null \n" +
//            " and enrollments.status = :status " +
//            " and course_subject.uuid = :courseSubjectUUID " +
//            " and students.deleted_at is null \n" +
//            " and subject_offered.deleted_at is null\n" +
//            " and course_subject.deleted_at is null \n" +
//            " and courses.deleted_at is null \n" +
//            " and subjects.deleted_at is null \n" +
//            " and semesters.deleted_at is null \n" +
//            " and academic_sessions.deleted_at is null \n" +
//            " and concat(academic_sessions.name,'|',courses.short_name,'|',subjects.code,'|',semesters.name,'|',students.student_id) \n" +
//            " ILIKE concat('%',:key,'%') ")
//    Mono<Long> countAllRecordsWithCourseSubjectAndStatusFilter(UUID courseSubjectUUID, Boolean status, String key);
}
