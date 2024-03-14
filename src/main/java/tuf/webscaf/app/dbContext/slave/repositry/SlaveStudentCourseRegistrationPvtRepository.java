package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentCourseRegistrationPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentCourseRegistrationPvtRepository extends ReactiveCrudRepository<SlaveStudentCourseRegistrationPvtEntity, Long> {
    Mono<SlaveStudentCourseRegistrationPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentCourseRegistrationPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


    /**
     * Count Number of courses that are not mapped yet against Student UUID with and without Status Filter
     **/
//    @Query("select count(*)\n" +
//            "FROM courses \n" +
//            "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//            "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//            "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//            "WHERE courses.uuid NOT IN (\n" +
//            "SELECT courses.uuid FROM courses\n" +
//            "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//            "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN student_course_registrations_pvt ON student_course_registrations_pvt.course_offered_uuid=course_offered.uuid \n" +
//            " WHERE student_course_registrations_pvt.student_uuid = :studentUUID\n" +
//            " AND student_course_registrations_pvt.deleted_at IS NULL\n" +
//            " AND courses.deleted_at IS NULL\n" +
//            " AND academic_sessions.deleted_at IS NULL\n" +
//            " AND course_offered.deleted_at IS NULL\n" +
//            ") \n" +
//            " AND courses.deleted_at IS NULL\n" +
//            "\t\t\t AND academic_sessions.deleted_at IS NULL\n" +
//            " AND course_offered.deleted_at IS NULL\n" +
//            "AND course_types.deleted_at IS NULL \n" +
//            "AND course_levels.deleted_at IS NULL \n" +
//            "AND (courses.name ILIKE concat('%',:name,'%' )" +
//            "or courses.slug ILIKE concat('%',:slug,'%') " +
//            "or courses.short_name ILIKE concat('%',:shortName,'%')" +
//            "or courses.description ILIKE concat('%',:description,'%') " +
//            "or courses.code ILIKE concat('%',:code,'%')" +
//            "or course_levels.name ILIKE concat('%',:courseLevelName,'%')" +
//            "or course_types.name ILIKE concat('%',:courseTypeName,'%'))")
//    Mono<Long> countExistingCoursesForStudentRegistration(UUID studentUUID, String name, String description, String slug, String shortName, String code, String courseLevelName, String courseTypeName);
//
//
//    @Query("select count(*)\n" +
//            "FROM courses \n" +
//            "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//            "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//            "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//            "WHERE courses.uuid NOT IN (\n" +
//            "SELECT courses.uuid FROM courses\n" +
//            "LEFT JOIN course_offered ON courses.uuid=course_offered.course_uuid\n" +
//            "LEFT JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN student_course_registrations_pvt ON student_course_registrations_pvt.course_offered_uuid=course_offered.uuid \n" +
//            " WHERE student_course_registrations_pvt.student_uuid = :studentUUID\n" +
//            " AND student_course_registrations_pvt.deleted_at IS NULL\n" +
//            " AND courses.deleted_at IS NULL\n" +
//            " AND academic_sessions.deleted_at IS NULL\n" +
//            " AND course_offered.deleted_at IS NULL\n" +
//            ") \n" +
//            " AND courses.deleted_at IS NULL\n" +
//            " AND courses.status = :status \n" +
//            " AND academic_sessions.deleted_at IS NULL\n" +
//            " AND course_offered.deleted_at IS NULL\n" +
//            "AND course_types.deleted_at IS NULL \n" +
//            "AND course_levels.deleted_at IS NULL \n" +
//            "AND (courses.name ILIKE concat('%',:name,'%' )" +
//            "or courses.slug ILIKE concat('%',:slug,'%') " +
//            "or courses.short_name ILIKE concat('%',:shortName,'%')" +
//            "or courses.description ILIKE concat('%',:description,'%') " +
//            "or courses.code ILIKE concat('%',:code,'%')" +
//            "or course_levels.name ILIKE concat('%',:courseLevelName,'%')" +
//            "or course_types.name ILIKE concat('%',:courseTypeName,'%'))")
//    Mono<Long> countExistingCoursesForStudentRegistrationWithStatus(UUID studentUUID, Boolean status, String name, String description, String slug, String shortName, String code, String courseLevelName, String courseTypeName);
//
//
//    /**
//     * Count Courses that are mapped for Given Student UUID with and without Status Filter
//     **/
//    @Query("select count(*)\n" +
//            "from courses" +
//            " left join course_offered on courses.uuid = course_offered.course_uuid\n" +
//            "left join student_course_registrations_pvt on course_offered.uuid=student_course_registrations_pvt.course_offered_uuid" +
//            " left JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//            "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//            "where student_course_registrations_pvt.student_uuid= :studentUUID\n" +
//            "and course_offered.deleted_at IS NULL\n" +
//            "and student_course_registrations_pvt.deleted_at IS NULL\n" +
//            "and courses.deleted_at IS NULL\n" +
//            "and course_types.deleted_at IS NULL\n" +
//            "and academic_sessions.deleted_at IS NULL " +
//            "AND (courses.name ILIKE concat('%',:courseName,'%') \n" +
//            "OR courses.slug  ILIKE concat('%',:courseSlug,'%') \n" +
//            "OR courses.short_name ILIKE concat('%',:courseShortName,'%') \n" +
//            "OR courses.description ILIKE concat('%',:courseDescription,'%') \n" +
//            "OR courses.code ILIKE concat('%',:courseCode,'%')  \n" +
//            "OR course_levels.name ILIKE concat('%',:searchCourseLevelName,'%') \n" +
//            "OR course_types.name  ILIKE concat('%',:searchCourseTypeName,'%')  \n" +
//            ")")
//    Mono<Long> countMappedStudentCourses(UUID studentUUID, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName);
//
//    // query used for count of mapped courses records for given Student UUID
//    @Query("select count(*)\n" +
//            "from courses" +
//            " left join course_offered on courses.uuid = course_offered.course_uuid\n" +
//            "left join student_course_registrations_pvt on course_offered.uuid=student_course_registrations_pvt.course_offered_uuid" +
//            " left JOIN academic_sessions ON course_offered.academic_session_uuid=academic_sessions.uuid\n" +
//            "LEFT JOIN course_types ON course_types.uuid=courses.course_type_uuid\n" +
//            "LEFT JOIN course_levels ON course_levels.uuid=courses.course_level_uuid\n" +
//            "where student_course_registrations_pvt.student_uuid= :studentUUID\n" +
//            "and course_offered.deleted_at IS NULL\n" +
//            "and student_course_registrations_pvt.deleted_at IS NULL\n" +
//            "and courses.deleted_at IS NULL\n" +
//            "and courses.status = :status \n" +
//            "and course_types.deleted_at IS NULL\n" +
//            "and academic_sessions.deleted_at IS NULL " +
//            "AND (courses.name ILIKE concat('%',:courseName,'%') \n" +
//            "OR courses.slug  ILIKE concat('%',:courseSlug,'%') \n" +
//            "OR courses.short_name ILIKE concat('%',:courseShortName,'%') \n" +
//            "OR courses.description ILIKE concat('%',:courseDescription,'%') \n" +
//            "OR courses.code ILIKE concat('%',:courseCode,'%')  \n" +
//            "OR course_levels.name ILIKE concat('%',:searchCourseLevelName,'%') \n" +
//            "OR course_types.name  ILIKE concat('%',:searchCourseTypeName,'%')  \n" +
//            ")")
//    Mono<Long> countMappedStudentCoursesWithStatus(UUID campusUUID, Boolean status, String courseName, String courseSlug, String courseDescription, String courseCode, String courseShortName, String searchCourseLevelName, String searchCourseTypeName);
}
