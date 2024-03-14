package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseSubjectDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseSubjectEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseSubjectRepository;

import java.util.UUID;

@Repository
public interface SlaveCourseSubjectRepository extends ReactiveCrudRepository<SlaveCourseSubjectEntity, Long>, SlaveCustomCourseSubjectRepository {

    Mono<SlaveCourseSubjectEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveCourseSubjectEntity> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select course_subject.*, concat(courses.name,'|',subjects.name) as name\n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "where course_subject.uuid = :uuid " +
            "and courses.deleted_at is null " +
            "and subjects.deleted_at is null" +
            " and course_subject.deleted_at is null")
    Mono<SlaveCourseSubjectDto> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%')\n " +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecords(String name);


    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.status = :status \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithStatus(String name, Boolean status);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.obe = :obe \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithObe(String name, Boolean obe);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.status = :status \n" +
            " and course_subject.obe = :obe \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithStatusAndObe(String name, Boolean status, Boolean obe);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.course_uuid = :courseUUID \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithCourse(String name, UUID courseUUID);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.course_uuid = :courseUUID \n" +
            " and course_subject.status = :status \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithCourseAndStatus(String name, UUID courseUUID, Boolean status);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.course_uuid = :courseUUID \n" +
            " and course_subject.obe = :obe \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithCourseAndObe(String name, UUID courseUUID, Boolean obe);

    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " where concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and course_subject.course_uuid = :courseUUID \n" +
            " and course_subject.status = :status \n" +
            " and course_subject.obe = :obe \n" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithCourseAndObeAndStatus(String name, UUID courseUUID, Boolean obe, Boolean status);

    //   count course-subject against department and status filter
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " join departments  on departments.uuid = courses.department_uuid\n" +
            " where departments.uuid = :departmentUUID\n" +
            " and course_subject.status = :status \n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and departments.deleted_at is null" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithDepartmentAndStatus(String name, UUID departmentUUID, Boolean status);

    //    count course-subject against department
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " join departments  on departments.uuid = courses.department_uuid\n" +
            " where departments.uuid = :departmentUUID\n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and departments.deleted_at is null" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithDepartment(String name, UUID departmentUUID);

    //   count course-subject against department and obe filter
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " join departments  on departments.uuid = courses.department_uuid\n" +
            " where departments.uuid = :departmentUUID\n" +
            " and course_subject.obe = :obe \n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and departments.deleted_at is null" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithDepartmentAndObe(String name, UUID departmentUUID, Boolean obe);

    //   count course-subject against department,obe and status filter
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid \n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            " join departments  on departments.uuid = courses.department_uuid\n" +
            " where departments.uuid = :departmentUUID\n" +
            " and course_subject.status = :status \n" +
            " and course_subject.obe = :obe \n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and departments.deleted_at is null" +
            " and courses.deleted_at is null \n" +
            " and subjects.deleted_at is null \n" +
            " and course_subject.deleted_at is null")
    Mono<Long> countAllRecordsWithDepartmentAndObeAndStatus(String name, UUID departmentUUID, Boolean obe, Boolean status);

    //   count course-subject against academicSession and status filter
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
            "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join subject_offered  on subject_offered.course_subject_uuid = course_subject.uuid\n" +
            "join academic_sessions on subject_offered.academic_session_uuid = academic_sessions.uuid\n" +
            "and course_offered.academic_session_uuid = academic_sessions.uuid\n" +
            "where subject_offered.academic_session_uuid = course_offered.academic_session_uuid\n" +
            "and academic_sessions.uuid = :academicSessionUUID" +
            " and course_subject.status = :status \n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n")
    Mono<Long> countAllRecordsWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status);

    //    count course-subject against academicSession
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
            "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join subject_offered  on subject_offered.course_subject_uuid = course_subject.uuid\n" +
            "join academic_sessions on subject_offered.academic_session_uuid = academic_sessions.uuid\n" +
            "and course_offered.academic_session_uuid = academic_sessions.uuid\n" +
            "where subject_offered.academic_session_uuid = course_offered.academic_session_uuid\n" +
            "and academic_sessions.uuid = :academicSessionUUID" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and subject_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n")
    Mono<Long> countAllRecordsWithAcademicSession(String name, UUID academicSessionUUID);

    //   count course-subject of course offered against academicSession and status filter
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
            "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "where course_offered.academic_session_uuid = :academicSessionUUID" +
            " and course_subject.status = :status \n" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n")
    Mono<Long> countCourseSubjectIndexOfOfferedCoursesWithAcademicSessionAndStatus(String name, UUID academicSessionUUID, Boolean status);

    //   count course-subject of course offered against academicSession
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join campus_course  on courses.uuid = campus_course.course_uuid\n" +
            "join course_offered  on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "where course_offered.academic_session_uuid = :academicSessionUUID" +
            " and concat(courses.name,'|',subjects.name) ILIKE concat('%',:name,'%') \n" +
            " and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n")
    Mono<Long> countCourseSubjectIndexOfOfferedCoursesWithAcademicSession(String name, UUID academicSessionUUID);

    //   count course-subject against academicSession and teacher
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
            "where teacher_subjects.academic_session_uuid =:academicSessionUUID " +
            "and teacher_subjects.teacher_uuid = :teacherUUID " +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            " AND \n" +
            " CASE \n" +
            "    WHEN course_subject.obe" +
            "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:key,'%') " +
            "    ELSE " +
            "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:key,'%') " +
            " END \n")
    Mono<Long> countCourseSubjectAgainstSessionAndTeacher(String key, UUID academicSessionUUID, UUID teacherUUID);

    //   count course-subject against academicSession and teacher
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
            "where teacher_subjects.academic_session_uuid =:academicSessionUUID " +
            "and teacher_subjects.teacher_uuid = :teacherUUID " +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and course_subject.status =:status\n" +
            " AND \n" +
            " CASE \n" +
            "    WHEN course_subject.obe" +
            "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:key,'%') " +
            "    ELSE " +
            "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:key,'%') " +
            " END \n")
    Mono<Long> countCourseSubjectAgainstSessionAndTeacherAndStatus(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status);

    //   count course-subject against academicSession and teacher
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
            "where teacher_subjects.academic_session_uuid =:academicSessionUUID " +
            "and teacher_subjects.teacher_uuid = :teacherUUID " +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and course_subject.status =:status\n" +
            "and academic_sessions.is_open =:openLms\n" +
            " AND \n" +
            " CASE \n" +
            "    WHEN course_subject.obe" +
            "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:key,'%') " +
            "    ELSE " +
            "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:key,'%') " +
            " END \n")
    Mono<Long> countCourseSubjectAgainstSessionAndTeacherAndStatusAndOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID, Boolean status, Boolean openLms);
    //   count course-subject against academicSession and teacher
    @Query("select count(*) \n" +
            "from course_subject \n" +
            "join courses  on courses.uuid = course_subject.course_uuid\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join subjects  on subjects.uuid = course_subject.subject_uuid\n" +
            "join teacher_subjects on teacher_subjects.course_subject_uuid= course_subject.uuid\n" +
            "join academic_sessions on teacher_subjects.academic_session_uuid = academic_sessions.uuid\n" +
            "where teacher_subjects.academic_session_uuid =:academicSessionUUID " +
            "and teacher_subjects.teacher_uuid = :teacherUUID " +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and course_subject.deleted_at is null\n" +
            "and teacher_subjects.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and academic_sessions.is_open =:openLms\n" +
            " AND \n" +
            " CASE \n" +
            "    WHEN course_subject.obe" +
            "    THEN concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'OBE') ILIKE concat('%',:key,'%') " +
            "    ELSE " +
            "    concat(academic_sessions.name, '|', course_levels.short_name, '|', courses.name,'|',subjects.name, '|', 'Non-OBE') ILIKE concat('%',:key,'%') " +
            " END \n")
    Mono<Long> countCourseSubjectAgainstSessionAndTeacherAndOpenLMS(String key, UUID academicSessionUUID, UUID teacherUUID,Boolean openLms);

}
