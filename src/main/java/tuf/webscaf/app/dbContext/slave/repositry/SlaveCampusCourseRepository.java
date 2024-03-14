package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusCourseDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCampusCourseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCampusCourseRepository;

import java.util.List;
import java.util.UUID;

@Repository

public interface SlaveCampusCourseRepository extends ReactiveCrudRepository<SlaveCampusCourseEntity, Long>, SlaveCustomCampusCourseRepository {
    Mono<SlaveCampusCourseEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query("select campus_course.*, concat(course_levels.short_name,'|',courses.name,'|',campuses.name) as name\n" +
            "from campus_course \n" +
            "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
            "join courses  on courses.uuid = campus_course.course_uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "where campus_course.uuid = :uuid " +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_levels.deleted_at is null " +
            "and campus_course.deleted_at is null")
    Mono<SlaveCampusCourseDto> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCampusCourseEntity> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select count(*) \n" +
            "from campus_course \n" +
            "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
            "join courses  on courses.uuid = campus_course.course_uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%') " +
            "and campuses.deleted_at is null " +
            "and course_levels.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and campus_course.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNull(String name);

    @Query("select count(*) \n" +
            "from campus_course \n" +
            "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
            "join courses  on courses.uuid = campus_course.course_uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%') " +
            "and campus_course.status = :status " +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_levels.deleted_at is null " +
            "and campus_course.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNullAndStatus(String name, Boolean status);

    /**
     * Count All Records With Campus Filter
     **/
    @Query("select count(*) \n" +
            "from campus_course \n" +
            "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
            "join courses  on courses.uuid = campus_course.course_uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%') " +
            "and campuses.deleted_at is null " +
            "and campus_course.campus_uuid = :campusUUID " +
            "and courses.deleted_at is null " +
            "and course_levels.deleted_at is null " +
            "and campus_course.deleted_at is null")
    Mono<Long> countAllCampusCourseWithCampusFilter(UUID campusUUID, String name);

    @Query("select count(*) \n" +
            "from campus_course \n" +
            "join campuses  on campuses.uuid = campus_course.campus_uuid \n" +
            "join courses  on courses.uuid = campus_course.course_uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%') " +
            "and campus_course.status = :status " +
            "and campus_course.campus_uuid = :campusUUID " +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_levels.deleted_at is null " +
            "and campus_course.deleted_at is null")
    Mono<Long> countAllCampusCourseWithCampusAndStatusFilter(UUID campusUUID, String name, Boolean status);

    @Query("select count(*) from campus_course\n" +
            "join campuses on campus_course.campus_uuid = campuses.uuid\n" +
            "join courses on campus_course.course_uuid = courses.uuid\n" +
            "join course_offered on course_offered.campus_course_uuid = campus_course.uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "where campus_course.campus_uuid =:campusUUID\n" +
            "and course_offered.academic_session_uuid = :academicSessionUUID\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and campuses.deleted_at is null\n" +
            "and courses.deleted_at is null\n" +
            "and course_levels.deleted_at is null " +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%') " +
            "and campus_course.status = :status ")
    Mono<Long> countCampusCourseListAgainstCampusAndAcademicSessionWithStatus(UUID campusUUID, UUID academicSessionUUID, String name, Boolean status);

    @Query("select count(*) from campus_course\n" +
            "join campuses on campus_course.campus_uuid = campuses.uuid\n" +
            "join courses on campus_course.course_uuid = courses.uuid\n" +
            "join course_levels  on course_levels.uuid = courses.course_level_uuid\n" +
            "join course_offered on course_offered.campus_course_uuid = campus_course.uuid\n" +
            "where campus_course.campus_uuid =:campusUUID\n" +
            "and course_offered.academic_session_uuid = :academicSessionUUID\n" +
            "and campus_course.deleted_at is null\n" +
            "and course_offered.deleted_at is null\n" +
            "and campuses.deleted_at is null\n" +
            "and course_levels.deleted_at is null " +
            "and courses.deleted_at is null\n" +
            "and concat(course_levels.short_name,'|',courses.name,'|',campuses.name) ILIKE concat('%',:name,'%')")
    Mono<Long> countCampusCoursesListAgainstCampusAndAcademicSession(UUID campusUUID, UUID academicSessionUUID, String name);

    Flux<SlaveCampusCourseEntity> findAllByCampusUUIDAndCourseUUIDInAndDeletedAtIsNull(UUID campusUUID, List<UUID> courseUUID);

    Flux<SlaveCampusCourseEntity> findAllByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<SlaveCampusCourseEntity> findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(UUID campusUUID, UUID courseUUID);

    Mono<SlaveCampusCourseEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);

    Mono<SlaveCampusCourseEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);
}
