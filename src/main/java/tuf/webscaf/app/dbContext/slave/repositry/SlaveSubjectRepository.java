package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectRepository;

import java.util.UUID;

@Repository
public interface SlaveSubjectRepository extends ReactiveCrudRepository<SlaveSubjectEntity, Long>, SlaveCustomSubjectRepository {

    Flux<SlaveSubjectEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String shortName, String description, String code);

    Flux<SlaveSubjectEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String shortName, Boolean status2, String description, Boolean status3, String code, Boolean status4);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNull(String name, String shortName, String description, String code);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String shortName, Boolean status2, String description, Boolean status3, String code, Boolean status4);

    Mono<SlaveSubjectEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //    used in seeder
    Mono<SlaveSubjectEntity> findByNameAndDeletedAtIsNull(String name);

    /**
     * Count Subjects Against Academic Session with and without status filter
     **/
    // query used for count of mapped subjects records for given course
    @Query("select count (*) from \n" +
            "(select distinct subjects.* \n" +
            "from subjects \n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "where subject_offered.academic_session_uuid = :academicSessionUUID \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            " and (subjects.name ILIKE concat('%',:name,'%') \n" +
            " or subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " or subjects.description ILIKE concat('%',:description,'%') \n" +
            " or subjects.code ILIKE concat('%',:code ,'%'))) AS count")
    Mono<Long> countSubjectAgainstAcademicSession(UUID academicSessionUUID, String name, String shortName, String description, String code);


    @Query("select count (*) from \n" +
            "(select distinct subjects.* \n" +
            "from subjects \n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "where subject_offered.academic_session_uuid = :academicSessionUUID \n" +
            "and subjects.status = :status " +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null\n" +
            " and (subjects.name ILIKE concat('%',:name,'%') \n" +
            " or subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " or subjects.description ILIKE concat('%',:description,'%') \n" +
            " or subjects.code ILIKE concat('%',:code ,'%'))) AS count")
    Mono<Long> countSubjectAgainstAcademicSessionWithStatusFilter(UUID academicSessionUUID, String name, String shortName, String description, String code, Boolean status);


    /**
     * Count Mapped Subjects against Course with or without status filter
     **/
    // query used for count of mapped subjects records for given course
    @Query("select count(*) from subjects\n" +
            "left join course_subject\n" +
            "on subjects.uuid = course_subject.subject_uuid\n" +
            "where course_subject.course_uuid = :courseUUID\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and (subjects.name ILIKE concat('%',:name,'%')\n" +
            "or subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "or subjects.description ILIKE concat('%',:description,'%')\n" +
            "or subjects.code ILIKE concat('%',:code ,'%')) \n")
    Mono<Long> countMappedCourseSubjects(UUID courseUUID, String name, String shortName, String description, String code);

    // query used for count of mapped subjects records for given course
    @Query("select count(*) from subjects\n" +
            "left join course_subject\n" +
            "on subjects.uuid = course_subject.subject_uuid\n" +
            "where course_subject.course_uuid = :courseUUID\n" +
            "and subjects.deleted_at is null\n" +
            "and subjects.status = :status " +
            "and course_subject.deleted_at is null\n" +
            "and (subjects.name ILIKE concat('%',:name,'%')\n" +
            "or subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "or subjects.description ILIKE concat('%',:description,'%')\n" +
            "or subjects.code ILIKE concat('%',:code ,'%')) \n")
    Mono<Long> countMappedCourseSubjectsWithStatus(UUID courseUUID, String name, String shortName, String description, String code, Boolean status);


    /**
     * Count Mapped Subjects against Course with or without status filter
     **/

    //query used in Course Subjects Pvt
    @Query("SELECT count(*) FROM subjects \n" +
            "WHERE subjects.uuid NOT IN(\n" +
            "SELECT subjects.uuid FROM subjects\n" +
            "LEFT JOIN course_subject\n" +
            "ON course_subject.subject_uuid = subjects.uuid \n" +
            "WHERE course_subject.course_uuid = :courseUUID\n" +
            "AND course_subject.deleted_at IS NULL\n" +
            "AND subjects.deleted_at IS NULL )\n" +
            "AND subjects.deleted_at IS NULL " +
            "AND (subjects.name ILIKE concat('%',:name,'%')\n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%')\n" +
            "OR subjects.code ILIKE concat('%',:code ,'%')) \n")
    Mono<Long> countExistingCourseSubjectsRecords(UUID courseUUID, String name, String shortName, String description, String code);

    //query used in Course Subjects Pvt With Status Filter
    @Query("SELECT count(*) FROM subjects \n" +
            "WHERE subjects.uuid NOT IN(\n" +
            "SELECT subjects.uuid FROM subjects\n" +
            "LEFT JOIN course_subject\n" +
            "ON course_subject.subject_uuid = subjects.uuid \n" +
            "WHERE course_subject.course_uuid = :courseUUID\n" +
            "AND course_subject.deleted_at IS NULL\n" +
            "AND subjects.deleted_at IS NULL )\n" +
            "AND subjects.deleted_at IS NULL " +
            "AND subjects.status = :status \n" +
            "AND (subjects.name ILIKE concat('%',:name,'%')\n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%')\n" +
            "OR subjects.code ILIKE concat('%',:code ,'%')) \n")
    Mono<Long> countExistingCourseSubjectsRecordsWithStatus(UUID courseUUID, String name, String shortName, String description, String code, Boolean status);


    /**
     * Count Mapped Subjects against Academic Session And Teacher Filter With and Without Status
     **/

    @Query("select count(*) \n" +
            "from subjects\n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
            "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid=course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
            "where academic_sessions.uuid= :academicSessionUUID\n" +
            "and teacher_subjects.teacher_uuid= :teacherUUID\n" +
            "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null " +
            "AND (subjects.name ILIKE concat('%',:name,'%') \n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%') \n" +
            "OR subjects.code ILIKE concat('%',:code ,'%') OR concat('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') ) \n")
    Mono<Long> countSubjectsAgainstAcademicSessionAndTeacher(UUID academicSessionUUID, UUID teacherUUID, String key, String name, String shortName, String description, String code);

    //query used in Course Subjects Pvt With Status Filter
    @Query("select count(*) \n" +
            "from subjects\n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
            "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid=course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
            "where academic_sessions.uuid= :academicSessionUUID\n" +
            "and teacher_subjects.teacher_uuid= :teacherUUID\n" +
            "and subjects.status = :status \n" +
            "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null " +
            "AND (subjects.name ILIKE concat('%',:name,'%') \n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%') \n" +
            "OR subjects.code ILIKE concat('%',:code ,'%') OR concat('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') ) \n")
    Mono<Long> countSubjectsAgainstAcademicSessionAndTeacherWithStatus(UUID academicSessionUUID, UUID teacherUUID, String key, String name, String shortName, String description, String code, Boolean status);

    /**
     * Count Subjects of Enrolled Students With & Without Status Filter (Used in LMS Module)
     **/
    @Query("select count(*)  from subjects \n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            "join courses on course_subject.course_uuid=courses.uuid \n" +
            "join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            "join enrollments on subject_offered.uuid=enrollments.subject_offered_uuid \n" +
            "join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            "join students on enrollments.student_uuid=students.uuid \n" +
            "join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            "where subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and enrollments.deleted_at is null \n" +
            "and students.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and semesters.deleted_at is null \n" +
            "and students.uuid= :studentUUID \n" +
            "and subjects.status= :status \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') " +
            " OR semesters.semester_no ILIKE concat('%',:semesterNo ,'%') " +
            " OR semesters.name ILIKE concat('%',:semesterName ,'%') ) \n")
    Mono<Long> countSubjectAgainstEnrolledStudentWithStatus(UUID studentUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo);

    @Query("select count(*)  from subjects \n" +
            " join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            " join enrollments on subject_offered.uuid=enrollments.subject_offered_uuid \n" +
            " join academic_sessions on enrollments.academic_session_uuid=academic_sessions.uuid \n" +
            " join students on enrollments.student_uuid=students.uuid \n" +
            " join semesters on enrollments.semester_uuid=semesters.uuid \n" +
            " where subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and enrollments.deleted_at is null \n" +
            " and students.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and students.uuid= :studentUUID \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') " +
            " OR semesters.semester_no ILIKE concat('%',:semesterNo ,'%') " +
            " OR semesters.name ILIKE concat('%',:semesterName ,'%') ) ")
    Mono<Long> countSubjectAgainstEnrolledStudentWithoutStatus(UUID studentUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo);


    /**
     * Count Subjects of Against Courses & Student With & Without Status Filter (Used in LMS Module)
     **/
    @Query("select count(*) from students \n" +
            " JOIN registrations ON registrations.student_uuid = students.uuid\n" +
            " JOIN campus_course ON registrations.campus_course_uuid = campus_course.uuid\n" +
            " JOIN enrollments ON enrollments.course_uuid= campus_course.course_uuid\n" +
            " AND enrollments.student_uuid = students.uuid\n" +
            " AND enrollments.campus_uuid = campus_course.campus_uuid\n" +
            " JOIN subject_offered ON enrollments.subject_offered_uuid = subject_offered.uuid\n" +
            " JOIN course_subject ON subject_offered.course_subject_uuid = course_subject.uuid\n" +
            " JOIN subjects ON course_subject.subject_uuid = subjects.uuid\n" +
            " JOIN courses ON course_subject.course_uuid=courses.uuid\n" +
            " JOIN semesters ON enrollments.semester_uuid=semesters.uuid\n" +
            " where  students.deleted_at is null \n" +
            " and subjects.status= :status \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and enrollments.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and students.uuid= :studentUUID \n" +
            " and courses.uuid= :courseUUID \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') " +
            " OR semesters.semester_no ILIKE concat('%',:semesterNo ,'%') " +
            " OR semesters.name ILIKE concat('%',:semesterName ,'%') ) ")
    Mono<Long> countSubjectAgainstCourseAndStudentWithStatus(UUID studentUUID, UUID courseUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo);

    @Query("select count(*) from students \n" +
            " JOIN registrations ON registrations.student_uuid = students.uuid\n" +
            " JOIN campus_course ON registrations.campus_course_uuid = campus_course.uuid\n" +
            " JOIN enrollments ON enrollments.course_uuid= campus_course.course_uuid\n" +
            " AND enrollments.student_uuid = students.uuid\n" +
            " AND enrollments.campus_uuid = campus_course.campus_uuid\n" +
            " JOIN subject_offered ON enrollments.subject_offered_uuid = subject_offered.uuid\n" +
            " JOIN course_subject ON subject_offered.course_subject_uuid = course_subject.uuid\n" +
            " JOIN subjects ON course_subject.subject_uuid = subjects.uuid\n" +
            " JOIN courses ON course_subject.course_uuid=courses.uuid\n" +
            " JOIN semesters ON enrollments.semester_uuid=semesters.uuid\n" +
            " where  students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and enrollments.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and semesters.deleted_at is null \n" +
            " and students.uuid= :studentUUID \n" +
            " and courses.uuid= :courseUUID \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') " +
            " OR semesters.semester_no ILIKE concat('%',:semesterNo ,'%') " +
            " OR semesters.name ILIKE concat('%',:semesterName ,'%') ) ")
    Mono<Long> countSubjectAgainstCourseAndStudentWithoutStatus(UUID studentUUID, UUID courseUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode, String semesterName, String semesterNo);

    /**
     * Count Subjects of Against Courses With & Without Status Filter (Used in LMS Module)
     **/
    @Query("select count(*) from subjects \n" +
            " join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
            " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid\n" +
            " join courses on course_subject.course_uuid=courses.uuid\n" +
            " where subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            "  and academic_sessions.deleted_at is null  \n" +
            " and courses.uuid= :courseUUID \n" +
            " and subjects.status= :status \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') ) ")
    Mono<Long> countSubjectAgainstCourseWithStatus(UUID courseUUID, Boolean status, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode);

    @Query("select count(*) from subjects \n" +
            " join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            " join subject_offered on course_subject.uuid=subject_offered.course_subject_uuid \n" +
            " join academic_sessions on subject_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " join courses on course_subject.course_uuid=courses.uuid \n" +
            " where subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null \n" +
            " and subject_offered.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            "  and academic_sessions.deleted_at is null  \n" +
            " and courses.uuid= :courseUUID \n" +
            " AND (subjects.name ILIKE concat('%',:subjectName,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:subjectCode ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') " +
            " OR courses.name ILIKE concat('%',:courseName ,'%') " +
            " OR courses.code ILIKE concat('%',:courseCode ,'%') ) ")
    Mono<Long> countSubjectAgainstCourseWithoutStatus(UUID courseUUID, String subjectName, String shortName, String description, String slug, String subjectCode, String courseName, String courseCode);

    @Query("select count(*) from subjects " +
            " where subjects.deleted_at is null " +
            " and subjects.status= :status " +
            " AND ( subjects.name ILIKE concat('%',:name,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') ) ")
    Mono<Long> countSubjectWithStatusFilter(Boolean status, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) from subjects " +
            " where subjects.deleted_at is null " +
            " AND ( subjects.name ILIKE concat('%',:name,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') ) ")
    Mono<Long> countSubjectWithoutStatusFilter(String name, String shortName, String code, String slug, String description);

    /**
     * Count Mapped Subjects against Academic Session And Teacher Filter With and Without Status (Where Academic Session OPEN LMS is true)
     **/

    @Query("Select count(*) from subjects \n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid \n" +
            "join subject_offered on subject_offered.course_subject_uuid=course_subject.uuid \n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid=course_subject.uuid \n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid \n" +
            "where teacher_subjects.teacher_uuid= :teacherUUID \n" +
            "and academic_sessions.uuid= :academicSessionUUID \n" +
            "and academic_sessions.is_open= :isOpenLMS \n" +
            "and subjects.status= :status \n" +
            "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid\n" +
            "and academic_sessions.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and teacher_subjects.deleted_at is null \n" +
            "AND (subjects.name ILIKE concat('%',:name,'%') \n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') OR concat('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') )")
    Mono<Long> countSubjectsAgainstAcademicSessionAndTeacherWithOpenLMSAndStatus(UUID academicSessionUUID, UUID teacherUUID, Boolean isOpenLMS, Boolean status, String key, String name, String shortName, String description, String code);

    @Query("Select count(*) from subjects\n" +
            "join course_subject on subjects.uuid=course_subject.subject_uuid\n" +
            "join subject_offered on subject_offered.course_subject_uuid=course_subject.uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid=course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid=academic_sessions.uuid\n" +
            "where teacher_subjects.teacher_uuid= :teacherUUID\n" +
            "and academic_sessions.uuid= :academicSessionUUID\n" +
            "and academic_sessions.is_open= :isOpenLMS \n" +
            "and teacher_subjects.academic_session_uuid = subject_offered.academic_session_uuid\n" +
            "and academic_sessions.deleted_at is null \n" +
            "and subject_offered.deleted_at is null \n" +
            "and subjects.deleted_at is null \n" +
            "and course_subject.deleted_at is null \n" +
            "and teacher_subjects.deleted_at is null \n" +
            "AND (subjects.name ILIKE concat('%',:name,'%') \n" +
            "OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            "OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') OR concat('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') )")
    Mono<Long> countSubjectsAgainstAcademicSessionAndTeacherWithOpenLMS(UUID academicSessionUUID, UUID teacherUUID, Boolean isOpenLMS, String key, String name, String shortName, String description, String code);

    @Query("select count(*) from subjects " +
            " join course_subject on subjects.uuid = course_subject.subject_uuid\n" +
            " join courses on courses.uuid = course_subject.course_uuid\n" +
            " join subject_offered on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            " join enrollments on enrollments.subject_offered_uuid = subject_offered.uuid\n" +
            " join students on enrollments.student_uuid = students.uuid\n" +
//            " join registrations on registrations.student_uuid = students.uuid\n" +
            " where enrollments.semester_uuid =  :semesterUUID\n" +
            " and courses.uuid = :courseUUID\n" +
            " and students.uuid = :studentUUID\n" +
//            " and registrations.deleted_at is null\n" +
            " and students.deleted_at is null\n" +
            " and enrollments.deleted_at is null\n" +
            " and courses.deleted_at is null\n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null\n" +
            " and subjects.deleted_at is null \n" +
            " and subjects.status= :status " +
            " AND ( subjects.name ILIKE concat('%',:name,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') ) ")
    Mono<Long> countSubjectsAgainstStudentCourseAndSemesterWithStatusFilter(Boolean status, String name, String shortName, String code, String slug, String description, UUID studentUUID, UUID courseUUID, UUID semesterUUID);

    @Query("select count(*) from subjects " +
            " join course_subject on subjects.uuid = course_subject.subject_uuid\n" +
            " join courses on courses.uuid = course_subject.course_uuid\n" +
            " join subject_offered on course_subject.uuid = subject_offered.course_subject_uuid\n" +
            " join enrollments on enrollments.subject_offered_uuid = subject_offered.uuid\n" +
            " join students on enrollments.student_uuid = students.uuid\n" +
//            " join registrations on registrations.student_uuid = students.uuid\n" +
            " where enrollments.semester_uuid =  :semesterUUID\n" +
            " and courses.uuid = :courseUUID\n" +
            " and students.uuid = :studentUUID\n" +
//            " and registrations.deleted_at is null\n" +
            " and students.deleted_at is null\n" +
            " and enrollments.deleted_at is null\n" +
            " and courses.deleted_at is null\n" +
            " and subject_offered.deleted_at is null\n" +
            " and course_subject.deleted_at is null\n" +
            " and subjects.deleted_at is null \n" +
            " AND ( subjects.name ILIKE concat('%',:name,'%') \n" +
            " OR subjects.description ILIKE concat('%',:description,'%') \n" +
            " OR subjects.short_name ILIKE concat('%',:shortName,'%') \n" +
            " OR subjects.code ILIKE concat('%',:code ,'%') " +
            " OR subjects.slug ILIKE concat('%',:slug ,'%') ) ")
    Mono<Long> countSubjectsAgainstStudentCourseAndSemesterWithoutStatusFilter(String name, String shortName, String code, String slug, String description, UUID studentUUID, UUID courseUUID, UUID semesterUUID);

}
