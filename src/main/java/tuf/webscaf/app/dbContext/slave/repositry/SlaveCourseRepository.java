package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseRepository;

import java.util.UUID;

@Repository
public interface SlaveCourseRepository extends ReactiveCrudRepository<SlaveCourseEntity, Long>, SlaveCustomCourseRepository {

    Mono<SlaveCourseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCourseEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String code, String shortName);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String code, String shortName);

    Flux<SlaveCourseEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2, String code, Boolean status3, String shortName, Boolean status4);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2, String code, Boolean status3, String shortName, Boolean status4);

    Mono<SlaveCourseEntity> findByIdAndDeletedAtIsNull(Long id);

    //    used in seeder
    Mono<SlaveCourseEntity> findByNameAndDeletedAtIsNull(String name);


    @Query("select count(*) from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
            "where registrations.student_uuid = :studentUUID" +
            " and courses.status = :status\n" +
            " and campus_course.deleted_at is null\n" +
            "and registrations.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsWithStudentAndStatusFilter(UUID studentUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join registrations on registrations.campus_course_uuid=campus_course.uuid\n" +
            "where registrations.student_uuid = :studentUUID" +
            " and campus_course.deleted_at is null\n" +
            "and registrations.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsWithStudentFilter(UUID studentUUID, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "where courses.status = :status\n" +
            "and course_levels.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            "or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsWithStatusFilter(Boolean status, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "where course_levels.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            "or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecords(String key, String name, String shortName, String code, String slug, String description);

    /**
     * Count Courses Against -Academic Session, Campus and Courses With and without Status Filter
     **/
    @Query("select count(*)\n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "where courses.uuid= :courseUUID" +
            " and campuses.uuid= :campusUUID" +
            " and academic_sessions.uuid= :academicSessionUUID" +
            " and courses.deleted_at is null\n" +
            " and courses.status = :status" +
            " and campus_course.deleted_at is null\n" +
            " and campuses.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            " and course_offered.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            "or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCampusAndCourseWithStatusFilter(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*)\n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join campuses on campus_course.campus_uuid=campuses.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "where courses.uuid= :courseUUID" +
            " and campuses.uuid= :campusUUID" +
            " and academic_sessions.uuid= :academicSessionUUID" +
            " and courses.deleted_at is null\n" +
            " and campus_course.deleted_at is null\n" +
            " and campuses.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            " and course_offered.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCampusAndCourseWithoutStatusFilter(UUID academicSessionUUID, UUID campusUUID, UUID courseUUID, String key, String name, String shortName, String code, String slug, String description);

    /**
     * Count Courses Against -Academic Session and Campus With and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " join course_offered on course_offered.campus_course_uuid=campus_course.uuid \n" +
            " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and campuses.uuid= :campusUUID" +
            " and courses.status = :status" +
            " and courses.deleted_at is null  \n" +
            " and campus_course.deleted_at is null  \n" +
            " and course_offered.deleted_at is null  \n" +
            " and campuses.deleted_at is null\n" +
            " and course_levels.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCampusWithStatusFilter(UUID academicSessionUUID, UUID campusUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid \n" +
            " join course_offered on course_offered.campus_course_uuid=campus_course.uuid \n" +
            " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_sessions.uuid= :academicSessionUUID " +
            " and campuses.uuid= :campusUUID" +
            " and courses.deleted_at is null  \n" +
            " and campus_course.deleted_at is null  \n" +
            "and course_levels.deleted_at is null\n" +
            " and course_offered.deleted_at is null  \n" +
            " and campuses.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCampusWithoutStatusFilter(UUID academicSessionUUID, UUID campusUUID, String key, String name, String shortName, String code, String slug, String description);

    /**
     * Count Courses Against -Academic Session and Courses With and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "where academic_sessions.uuid= :academicSessionUUID " +
            "and courses.uuid= :courseUUID" +
            " and courses.deleted_at is null\n" +
            " and courses.status = :status" +
            " and campus_course.deleted_at is null\n" +
            "and course_levels.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCourseWithStatusFilter(UUID academicSessionUUID, UUID courseUUID, Boolean status, String key, String name, String shortName, String code, String slug, String description);

    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            "join campus_course on campus_course.course_uuid=courses.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid=campus_course.uuid\n" +
            "join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid\n" +
            "where academic_sessions.uuid= :academicSessionUUID " +
            "and courses.uuid= :courseUUID" +
            " and courses.deleted_at is null\n" +
            " and course_levels.deleted_at is null\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countRecordsAgainstAcademicSessionCourseWithoutStatusFilter(UUID academicSessionUUID, UUID courseUUID, String key, String name, String shortName, String code, String slug, String description);

    /**
     * Count All records based on Campus and Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid  \n" +
            "  where campuses.uuid= :campusUUID \n" +
            " and courses.uuid= :courseUUID \n" +
            " and courses.status = :status \n" +
            "  and courses.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            "  and campus_course.deleted_at is null \n" +
            "  and campuses.deleted_at is null  \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countWithCourseAndCampusWithStatus(UUID campusUUID, UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status);


    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campus_course.campus_uuid=campuses.uuid  \n" +
            "  where campuses.uuid= :campusUUID \n" +
            " and courses.uuid= :courseUUID \n" +
            "  and courses.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            "  and campus_course.deleted_at is null \n" +
            "  and campuses.deleted_at is null  \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countWithCourseAndCampus(UUID campusUUID, UUID courseUUID, String key, String name, String shortName, String code, String slug, String description);


    /**
     * Count All records based on Academic Session filter (used in enrollments) --> with and without Status Filter
     **/

    @Query(" select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join course_offered on campus_course.uuid=course_offered.campus_course_uuid \n" +
            " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
            "  where academic_sessions.uuid= :academicSessionUUID \n" +
            " and course_offered.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null  \n" +
            " and course_levels.deleted_at is null\n" +
            " and courses.status = :status \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countAgainstSessionWithStatus(UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status);


    @Query(" select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join course_offered on campus_course.uuid=course_offered.campus_course_uuid \n" +
            " join academic_sessions on course_offered.academic_session_uuid=academic_sessions.uuid \n" +
            "  where academic_sessions.uuid= :academicSessionUUID \n" +
            " and course_offered.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null \n" +
            " and courses.deleted_at is null  \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countAgainstSessionWithoutStatus(UUID academicSessionUUID, String key, String name, String shortName, String code, String slug, String description);


    /**
     * Count All records based on Campus filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campuses.uuid=campus_course.campus_uuid \n" +
            " where campuses.uuid= :campusUUID \n" +
            " and courses.status= :status \n" +
            "  and campuses.deleted_at is null \n" +
            "  and courses.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            "  and campus_course.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countCampusWithStatus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status);


    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " join campuses on campuses.uuid=campus_course.campus_uuid \n" +
            " where campuses.uuid= :campusUUID \n" +
            "  and campuses.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            "  and campus_course.deleted_at is null \n" +
            "  and courses.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countCampusWithoutStatus(UUID campusUUID, String key, String name, String shortName, String code, String slug, String description);


    /**
     * Count All records based on Courses filter (used in enrollments) --> with and without Status Filter
     **/
    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " where courses.uuid= :courseUUID \n" +
            " and courses.status= :status \n" +
            " and courses.deleted_at is null  \n" +
            " and course_levels.deleted_at is null\n" +
            " and courses.deleted_at is null \n" +
            " and campus_course.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countCourseWithStatus(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description, Boolean status);

    @Query("select count(*) \n" +
            "from courses\n" +
            "join course_levels on courses.course_level_uuid = course_levels.uuid\n" +
            " join campus_course on campus_course.course_uuid=courses.uuid \n" +
            " where courses.uuid= :courseUUID \n" +
            " and courses.deleted_at is null  \n" +
            " and courses.deleted_at is null \n" +
            " and course_levels.deleted_at is null\n" +
            " and campus_course.deleted_at is null \n" +
            " AND (courses.name ILIKE concat('%',:name,'%') " +
            " or courses.description ILIKE concat('%',:description,'%') " +
            " or concat(course_levels.short_name,'|',courses.name) ILIKE concat('%',:key,'%') " +
            " or courses.code ILIKE concat('%',:code,'%')" +
            " or courses.slug ILIKE concat('%',:slug,'%')  " +
            " OR courses.short_name ILIKE concat('%',:shortName,'%'))")
    Mono<Long> countCourseWithoutStatus(UUID courseUUID, String key, String name, String shortName, String code, String slug, String description);


}
