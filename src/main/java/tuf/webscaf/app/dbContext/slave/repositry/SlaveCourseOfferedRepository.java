package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseOfferedDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseOfferedEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseOfferedRepository;

import java.util.UUID;

@Repository
public interface SlaveCourseOfferedRepository extends ReactiveCrudRepository<SlaveCourseOfferedEntity, Long>, SlaveCustomCourseOfferedRepository {

    Mono<SlaveCourseOfferedEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query("select course_offered.*, concat(academic_sessions.name,'|',campuses.name,'|',courses.name) as name\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where course_offered.uuid = :uuid " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<SlaveCourseOfferedDto> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCourseOfferedEntity> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNull(String name);

    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "and concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and course_offered.status= :status " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNullAndStatus(String name, Boolean status);

    /**
     * Count All Course offered Based on Campus UUID Filter With and Without Status Filter
     **/
    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and campuses.uuid = :campusUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithCampusFilter(UUID campusUUID, String name);

    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and course_offered.status= :status " +
            "and campuses.uuid= :campusUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithCampusAndStatusFilter(UUID campusUUID, String name, Boolean status);

    /**
     * Count All Course offered Based on Academic Session UUID Filter With and Without Status Filter
     **/
    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithAcademicSessionFilter(UUID academicSessionUUID, String name);

    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and course_offered.status= :status " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithAcademicSessionAndStatusFilter(UUID academicSessionUUID, String name, Boolean status);

    /**
     * Count All Course offered Based on Academic Session, Campus UUID Filter With and Without Status Filter
     **/
    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            "and campuses.uuid = :campusUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithSessionCampusFilter(UUID academicSessionUUID, UUID campusUUID, String name);

    @Query("select count(*)\n" +
            "from course_offered \n" +
            "join academic_sessions  on academic_sessions.uuid = course_offered.academic_session_uuid \n" +
            "join campus_course on campus_course.uuid = course_offered.campus_course_uuid\n" +
            "join campuses on campuses.uuid = campus_course.campus_uuid\n" +
            "join courses on courses.uuid = campus_course.course_uuid\n" +
            "where concat(academic_sessions.name,'|',campuses.name,'|',courses.name) ILIKE concat('%',:name,'%') " +
            "and course_offered.status= :status " +
            "and academic_sessions.uuid = :academicSessionUUID " +
            "and campuses.uuid = :campusUUID " +
            "and academic_sessions.deleted_at is null " +
            "and campus_course.deleted_at is null \n" +
            "and campuses.deleted_at is null " +
            "and courses.deleted_at is null " +
            "and course_offered.deleted_at is null")
    Mono<Long> countAllCourseOfferedWithCampusAcademicSessionAndStatusFilter(UUID academicSessionUUID, UUID campusUUID, String name, Boolean status);

    Mono<SlaveCourseOfferedEntity> findFirstByAcademicSessionUUIDAndDeletedAtIsNull(UUID academicSessionUUID);

}
