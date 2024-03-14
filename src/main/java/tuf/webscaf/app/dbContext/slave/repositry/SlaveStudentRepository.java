package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSectionStudentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentGroupStudentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentStudentProfileContactNoFacadeRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentRepository extends ReactiveCrudRepository<SlaveStudentEntity, Long>, SlaveCustomSectionStudentPvtRepository,
        SlaveCustomStudentGroupStudentPvtRepository, SlaveCustomStudentRepository, SlaveCustomStudentStudentProfileContactNoFacadeRepository {
    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String id);

    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String id, Boolean status);

    Mono<SlaveStudentEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(Pageable pageable, String studentID, List<UUID> uuid);

    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(Pageable pageable, String studentID, Boolean status, List<UUID> uuid);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(String studentID, List<UUID> uuid);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(String studentID, Boolean status, List<UUID> uuid);

    Mono<SlaveStudentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentEntity> findFirstByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndDeletedAtIsNull(String id);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String id, Boolean status);

    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndUuidIsNotAndDeletedAtIsNull(Pageable pageable, String studentId, UUID uuid);

    Flux<SlaveStudentEntity> findAllByStudentIdContainingIgnoreCaseAndUuidIsNotAndStatusAndDeletedAtIsNull(Pageable pageable, String studentId, UUID uuid, Boolean status);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndUuidIsNotAndDeletedAtIsNull(String studentId, UUID uuid);

    Mono<Long> countByStudentIdContainingIgnoreCaseAndUuidIsNotAndStatusAndDeletedAtIsNull(String studentId, UUID uuid, Boolean status);

    //    used in seeder
    Mono<SlaveStudentEntity> findByStudentIdAndDeletedAtIsNull(String stuid);

    Mono<Long> countByDeletedAtIsNull();

    /**
     * Count Students Records that are not Mapped against Student Group Yet with and without Status Filter
     **/
    //query used in Student Group Student  pvt mapping handler
    @Query("select count(*)\n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on campus_course.uuid=registrations.campus_course_uuid\n" +
            " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            " where students.uuid NOT IN (\n" +
            " SELECT students.uuid \n" +
            " from students \n" +
            " join section_student_pvt on students.uuid=section_student_pvt.student_uuid\n" +
            " join sections on sections.uuid=section_student_pvt.section_uuid\n" +
            " join course_offered on sections.course_offered_uuid=course_offered.uuid\n" +
            " where sections.course_offered_uuid = :courseOfferedUUID\n" +
            " and course_offered.uuid=sections.course_offered_uuid\n" +
            " and section_student_pvt.deleted_at is null\n" +
            " and students.deleted_at is null\n" +
            " and sections.deleted_at is null\n" +
            " and course_offered.deleted_at is null\n" +
            ")\n" +
            "and course_offered.uuid = :courseOfferedUUID\n" +
            "and students.deleted_at is null\n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and course_offered.deleted_at is null \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')" +
            "AND students.deleted_at IS NULL ")
    Mono<Long> countUnMappedRecords(UUID courseOfferedUUID, String studentId);

    //query used in Student Group Student  pvt mapping handler with Status Filter
    @Query("select count(*)\n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on campus_course.uuid=registrations.campus_course_uuid\n" +
            " join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            " where students.uuid NOT IN (\n" +
            " SELECT students.uuid \n" +
            " from students \n" +
            " join section_student_pvt on students.uuid=section_student_pvt.student_uuid\n" +
            " join sections on sections.uuid=section_student_pvt.section_uuid\n" +
            " join course_offered on sections.course_offered_uuid=course_offered.uuid\n" +
            " where  sections.course_offered_uuid = :courseOfferedUUID\n" +
            " and course_offered.uuid=sections.course_offered_uuid\n" +
            " and section_student_pvt.deleted_at is null\n" +
            " and students.deleted_at is null\n" +
            " and sections.deleted_at is null\n" +
            " and course_offered.deleted_at is null\n" +
            ")\n" +
            "and course_offered.uuid = :courseOfferedUUID\n" +
            "and students.deleted_at is null\n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and course_offered.deleted_at is null \n" +
            "AND students.status = :status \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')" +
            "AND students.deleted_at IS NULL ")
    Mono<Long> countUnMappedRecordsWithStatus(UUID courseOfferedUUID, String studentId, Boolean status);

    /**
     * Count Students that are mapped for Given Student Group UUID with and without Status Filter
     **/
    @Query("SELECT count(*) FROM students\n" +
            "JOIN section_student_pvt ON section_student_pvt.student_uuid = students.uuid\n" +
            "JOIN sections ON sections.uuid = section_student_pvt.section_uuid\n" +
            "JOIN course_offered ON course_offered.uuid = sections.course_offered_uuid\n" +
            "WHERE section_student_pvt.section_uuid = :sectionUUID\n" +
            "and course_offered_uuid=:courseOfferedUUID\n" +
            "AND section_student_pvt.deleted_at IS NULL\n" +
            "AND students.deleted_at IS NULL\n" +
            "AND sections.deleted_at IS NULL\n" +
            "AND course_offered.deleted_at IS NULL\n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')")
    Mono<Long> countMappedStudentAgainstSection(UUID sectionUUID, UUID courseOfferedUUID, String studentId);

    // query used for count of mapped students records for given section UUID and courseOffered
    @Query("SELECT count(*) FROM students\n" +
            "JOIN section_student_pvt ON section_student_pvt.student_uuid = students.uuid\n" +
            "JOIN sections ON sections.uuid = section_student_pvt.section_uuid\n" +
            "JOIN course_offered ON course_offered.uuid = sections.course_offered_uuid\n" +
            "WHERE section_student_pvt.section_uuid = :sectionUUID\n" +
            "and course_offered_uuid=:courseOfferedUUID\n" +
            "AND section_student_pvt.deleted_at IS NULL\n" +
            "AND students.deleted_at IS NULL\n" +
            "AND sections.deleted_at IS NULL\n" +
            "AND course_offered.deleted_at IS NULL\\n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')" +
            "and students.status = :status ")
    Mono<Long> countMappedStudentAgainstSectionWithStatus(UUID sectionUUID, UUID courseOfferedUUID, String studentId, Boolean status);


    /**
     * Count Mapped Students against Student Group with or without status filter
     **/

    // query used for count of mapped students records for given student group
    @Query("select count(*) from students\n" +
            "left join student_group_students_pvt\n" +
            "on students.uuid = student_group_students_pvt.student_uuid\n" +
            "where student_group_students_pvt.student_group_uuid = :studentGroupUUID\n" +
            "and students.deleted_at is null\n" +
            "and student_group_students_pvt.deleted_at is null\n" +
            "and (students.student_id ILIKE concat('%',:studentId,'%') " +
            "or students.official_email ILIKE  concat('%',:officialEmail,'%') )")
    Mono<Long> countMappedStudentGroupStudents(UUID studentGroupUUID, String studentId, String officialEmail);

    // query used for count of mapped students records for given student group
    @Query("select count(*) from students\n" +
            "left join student_group_students_pvt\n" +
            "on students.uuid = student_group_students_pvt.student_uuid\n" +
            "where student_group_students_pvt.student_group_uuid = :studentGroupUUID\n" +
            "and students.deleted_at is null\n" +
            "and students.status = :status " +
            "and student_group_students_pvt.deleted_at is null\n" +
            "and (students.student_id ILIKE concat('%',:studentId,'%') " +
            "or students.official_email ILIKE  concat('%',:officialEmail,'%') )")
    Mono<Long> countMappedStudentGroupStudentsWithStatus(UUID studentGroupUUID, String studentId, String officialEmail, Boolean status);


    /**
     * Count Unmapped Students against Student Group with or without status filter
     **/

    //query used in pvt Student Group Students Pvt
    @Query("SELECT count(*) FROM students \n" +
            "WHERE students.uuid NOT IN(\n" +
            "SELECT students.uuid FROM students\n" +
            "LEFT JOIN student_group_students_pvt\n" +
            "ON student_group_students_pvt.student_uuid = students.uuid \n" +
            "WHERE student_group_students_pvt.student_group_uuid = :studentGroupUUID\n" +
            "AND student_group_students_pvt.deleted_at IS NULL\n" +
            "AND students.deleted_at IS NULL )\n" +
            "AND students.deleted_at IS NULL " +
            "AND (students.student_id ILIKE concat('%',:studentId,'%') " +
            "OR students.official_email ILIKE concat('%',:officialEmail ,'%')) \n")
    Mono<Long> countUnMappedStudentGroupStudentsRecords(UUID studentGroupUUID, String studentId, String officialEmail);

    //query used in pvt Student Group Students Pvt
    @Query("SELECT count(*) FROM students \n" +
            "WHERE students.uuid NOT IN(\n" +
            "SELECT students.uuid FROM students\n" +
            "LEFT JOIN student_group_students_pvt\n" +
            "ON student_group_students_pvt.student_uuid = students.uuid \n" +
            "WHERE student_group_students_pvt.student_group_uuid = :studentGroupUUID\n" +
            "AND student_group_students_pvt.deleted_at IS NULL\n" +
            "AND students.deleted_at IS NULL )\n" +
            "AND students.deleted_at IS NULL " +
            "AND students.status = :status \n" +
            "AND (students.student_id ILIKE concat('%',:studentId,'%') " +
            "OR students.official_email ILIKE concat('%',:officialEmail ,'%')) \n")
    Mono<Long> countUnMappedStudentGroupStudentsRecordsWithStatus(UUID studentGroupUUID, String studentId, String officialEmail, Boolean status);

    @Query("select count(*) from students\n" +
            "join registrations on students.uuid = registrations.student_uuid\n" +
            "join campus_course on campus_course.uuid = registrations.campus_course_uuid\n" +
            "join course_offered on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "where course_offered.uuid = :courseOfferedUUID \n" +
            "and students.deleted_at IS NULL\n" +
            "and registrations.deleted_at IS NULL\n" +
            "and campus_course.deleted_at IS NULL \n" +
            "and course_offered.deleted_at IS NULL\n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')")
    Mono<Long> countWithCourseOffered(UUID courseOfferedUUID, String studentId);

    @Query("select count(*) from students\n" +
            "join registrations on students.uuid = registrations.student_uuid\n" +
            "join campus_course on campus_course.uuid = registrations.campus_course_uuid\n" +
            "join course_offered on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "where course_offered.uuid = :courseOfferedUUID \n" +
            "and students.status = :status \n" +
            "and students.deleted_at IS NULL\n" +
            "and registrations.deleted_at IS NULL\n" +
            "and campus_course.deleted_at IS NULL \n" +
            "and course_offered.deleted_at IS NULL\n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')")
    Mono<Long> countWithCourseOfferedWithStatus(UUID courseOfferedUUID, String studentId, Boolean status);

    /**
     * Count All records based on course Academic Session and Campus filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from students \n" +
            " join registrations on registrations.student_uuid=students.uuid \n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            " join courses on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and courses.uuid = :courseUUID \n" +
            " and academic_sessions.uuid = :academicSessionUUID \n" +
            " and campuses.uuid = :campusUUID \n" +
            " and students.status = :status \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')")
    Mono<Long> countWithCampusCourseAndAcademicSessionWithStatus(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, String studentId, Boolean status);


    @Query("select count(*) \n" +
            " from students \n" +
            " join registrations on registrations.student_uuid=students.uuid \n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            " join courses on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and courses.uuid = :courseUUID \n" +
            " and academic_sessions.uuid = :academicSessionUUID \n" +
            " and campuses.uuid = :campusUUID \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%')")
    Mono<Long> countWithCampusCourseAndAcademicSession(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, String studentId);

    /**
     * Count All records based on Academic Session and Campus filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid \n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            "where students.deleted_at is null \n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and campuses.deleted_at is null \n" +
            "and academic_sessions.uuid= :academicSessionUUID \n" +
            " and campuses.uuid = :campusUUID \n" +
            " and students.status = :status \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCampusAndAcademicSessionWithStatus(UUID academicSessionUUID, UUID campusUUID, String studentId, Boolean status);


    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid \n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            "where students.deleted_at is null \n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and campuses.deleted_at is null \n" +
            "and academic_sessions.uuid= :academicSessionUUID \n" +
            " and campuses.uuid = :campusUUID \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCampusAndAcademicSession(UUID academicSessionUUID, UUID campusUUID, String studentId);

    /**
     * Count All records based on Academic Session and Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid \n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            "join courses on campus_course.course_uuid=courses.uuid \n" +
            "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            "where students.deleted_at is null \n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and academic_sessions.uuid= :academicSessionUUID \n" +
            " and courses.uuid = :courseUUID \n" +
            " and students.status = :status \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCourseAndAcademicSessionWithStatus(UUID academicSessionUUID, UUID courseUUID, String studentId, Boolean status);


    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid \n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            "join courses on campus_course.course_uuid=courses.uuid \n" +
            "join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            "where students.deleted_at is null \n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and academic_sessions.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and academic_sessions.uuid= :academicSessionUUID \n" +
            " and courses.uuid = :courseUUID \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCourseAndAcademicSession(UUID academicSessionUUID, UUID courseUUID, String studentId);

    /**
     * Count All records based on Campus and Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid \n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            "join courses on campus_course.course_uuid=courses.uuid \n" +
            "join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            "where students.deleted_at is null \n" +
            "and registrations.deleted_at is null \n" +
            "and campus_course.deleted_at is null \n" +
            "and courses.deleted_at is null \n" +
            "and campuses.deleted_at is null \n" +
            " and courses.uuid = :courseUUID \n" +
            " and campuses.uuid = :campusUUID \n" +
            " and students.status = :status \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCourseAndCampusWithStatus(UUID campusUUID, UUID courseUUID, String studentId, Boolean status);


    @Query("select count(*)  \n" +
            " from students \n" +
            " join registrations on registrations.student_uuid=students.uuid \n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid \n" +
            " join courses on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and campuses.uuid= :campusUUID \n" +
            " and courses.uuid = :courseUUID \n" +
            "AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countWithCourseAndCampus(UUID campusUUID, UUID courseUUID, String studentId);


    /**
     * Count All records based on Academic Session filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*)  \n" +
            " from students \n" +
            " join registrations on registrations.student_uuid=students.uuid \n" +
            " join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null\n" +
            " and academic_sessions.uuid= :academicSessionUUID \n" +
            " and students.status = :status \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countAgainstSessionWithStatus(UUID academicSessionUUID, String studentId, Boolean status);


    @Query("select count(*)  \n" +
            " from students \n" +
            " join registrations on registrations.student_uuid=students.uuid \n" +
            " join academic_sessions on registrations.academic_session_uuid=academic_sessions.uuid \n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null\n" +
            " and academic_sessions.uuid= :academicSessionUUID \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countAgainstSessionWithoutStatus(UUID academicSessionUUID, String studentId);


    /**
     * Count All records based on Campus filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid\n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and campuses.uuid= :academicSessionUUID \n" +
            " and students.status = :status \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countCampusWithStatus(UUID campusUUID, String studentId, Boolean status);


    @Query("select count(*) \n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid\n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and campuses.deleted_at is null \n" +
            " and campuses.uuid= :academicSessionUUID \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countCampusWithoutStatus(UUID campusUUID, String studentId);


    /**
     * Count All records based on Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            " join courses on campus_course.course_uuid=courses.uuid\n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and courses.uuid= :courseUUID \n" +
            " and students.status = :status \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countCourseWithStatus(UUID courseUUID, String studentId, Boolean status);


    @Query("select count(*) \n" +
            " from students\n" +
            " join registrations on registrations.student_uuid=students.uuid\n" +
            " join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            " join courses on campus_course.course_uuid=courses.uuid\n" +
            " where students.deleted_at is null \n" +
            " and registrations.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and courses.deleted_at is null \n" +
            " and courses.uuid= :courseUUID \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countCourseWithoutStatus(UUID campusUUID, String studentId);


    /**
     * Count All Student Records with and without Status Filter
     **/
    @Query("select count(*) \n" +
            " from students \n" +
            " where students.deleted_at is null \n" +
            " and students.status = :status \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countStudentsWithStatus(String studentId, Boolean status);


    @Query("select count(*) \n" +
            " from students \n" +
            " where students.deleted_at is null \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countStudentsWithoutStatus(String studentId);

    @Query("select distinct count(*) \n" +
            " from students \n" +
            " left join std_profiles on students.uuid = std_profiles.student_uuid \n" +
            " where students.deleted_at is null \n" +
            " and std_profiles.deleted_at is null \n" +
            " and students.status = :status \n" +
            " AND (std_profiles.first_name ILIKE concat('%',:firstname,'%') " +
            " or std_profiles.last_name ILIKE concat('%',:lastname,'%') " +
            " or students.student_id ILIKE concat('%',:studentId,'%') " +
            "or std_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentStudentProfileContactNoWithStatus(String firstname, String lastname, String studentId, String nic, Boolean status);

    @Query("select count(*) \n" +
            " from students \n" +
            " join std_profiles on students.uuid = std_profiles.student_uuid \n" +
            " where students.deleted_at IS NULL\n" +
            " AND std_profiles.deleted_at IS NULL\n" +
            " and (std_profiles.first_name ILIKE concat('%',:firstname,'%') " +
            " or std_profiles.last_name ILIKE concat('%',:lastname,'%') " +
            " or students.student_id ILIKE concat('%',:studentId,'%') " +
            " or std_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentStudentProfileContactNoWithOutStatus(String firstname, String lastname, String studentId, String nic);


    /**
     * Count Students Against Commencement Of Classes Against teacher , Course Subject and Academic Session (This is used by LMS in Assignment Attempt Handler)
     **/
    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid\n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "join enrollments on enrollments.student_uuid=students.uuid\n" +
            "join subject_offered on subject_offered.uuid=enrollments.subject_offered_uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join commencement_of_classes on subjects.uuid=commencement_of_classes.subject_uuid\n" +
            "join teachers on teachers.uuid=commencement_of_classes.teacher_uuid\n" +
            "where students.deleted_at is null\n" +
            "and  course_offered.academic_session_uuid=subject_offered.academic_session_uuid\n" +
            "and  enrollments.uuid=commencement_of_classes.enrollment_uuid\n" +
            "and  academic_sessions.uuid=commencement_of_classes.academic_session_uuid\n" +
            "and  registrations.deleted_at is null\n" +
            "and  enrollments.deleted_at is null \n" +
            "and  campus_course.deleted_at is null \n" +
            "and  course_offered.deleted_at is null \n" +
            "and  academic_sessions.deleted_at is null \n" +
            "and  subject_offered.deleted_at is null \n" +
            "and  course_subject.deleted_at is null \n" +
            "and  subjects.deleted_at is null \n" +
            "and  courses.deleted_at is null \n" +
            "and  commencement_of_classes.deleted_at is null \n" +
            "and  enrollments.deleted_at is null \n" +
            "and  teachers.deleted_at is null \n" +
            "and teachers.uuid = :teacherUUID \n" +
            "and course_subject_uuid = :courseSubjectUUID \n" +
            "and academic_sessions.uuid = :academicSessionUUID \n" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countAllStudentsAgainstTeacherWithSameCourseSubjectAndSession(UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId);

    @Query("select count(*) \n" +
            "from students \n" +
            "join registrations on registrations.student_uuid=students.uuid\n" +
            "join campus_course on registrations.campus_course_uuid=campus_course.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "join enrollments on enrollments.student_uuid=students.uuid\n" +
            "join subject_offered on subject_offered.uuid=enrollments.subject_offered_uuid\n" +
            "join course_subject on course_subject.uuid=subject_offered.course_subject_uuid\n" +
            "join subjects on subjects.uuid=course_subject.subject_uuid\n" +
            "join courses on courses.uuid=course_subject.course_uuid\n" +
            "join commencement_of_classes on subjects.uuid=commencement_of_classes.subject_uuid\n" +
            "join teachers on teachers.uuid=commencement_of_classes.teacher_uuid\n" +
            "where students.deleted_at is null\n" +
            "and  course_offered.academic_session_uuid=subject_offered.academic_session_uuid\n" +
            "and  enrollments.uuid=commencement_of_classes.enrollment_uuid\n" +
            "and  academic_sessions.uuid=commencement_of_classes.academic_session_uuid\n" +
            "and  registrations.deleted_at is null \n" +
            "and  enrollments.deleted_at is null \n" +
            "and  campus_course.deleted_at is null \n" +
            "and  course_offered.deleted_at is null \n" +
            "and  academic_sessions.deleted_at is null \n" +
            "and  subject_offered.deleted_at is null \n" +
            "and  course_subject.deleted_at is null \n" +
            "and  subjects.deleted_at is null \n" +
            "and  courses.deleted_at is null \n" +
            "and  commencement_of_classes.deleted_at is null \n" +
            "and  enrollments.deleted_at is null \n" +
            "and  teachers.deleted_at is null \n" +
            "and teachers.uuid = :teacherUUID \n" +
            "and course_subject_uuid = :courseSubjectUUID \n" +
            "and academic_sessions.uuid = :academicSessionUUID and students.status= :status" +
            " AND students.student_id ILIKE concat('%',:studentId,'%') ")
    Mono<Long> countAllStudentsAgainstTeacherWithSameCourseSubjectAndSessionAndStatus(Boolean status, UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, String studentId);
}
