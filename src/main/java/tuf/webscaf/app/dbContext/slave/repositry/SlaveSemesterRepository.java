package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSemesterEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSemesterRepository;

import java.util.UUID;

@Repository
public interface SlaveSemesterRepository extends ReactiveCrudRepository<SlaveSemesterEntity, Long>, SlaveCustomSemesterRepository {
    Mono<SlaveSemesterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveSemesterEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Flux<SlaveSemesterEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Mono<SlaveSemesterEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    @Query("select count(*) from semesters\n" +
            "join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid \n" +
            " where academic_calendar_semesters.academic_calendar_uuid = :academicCalendarUUID\n" +
            " and semesters.deleted_at is null\n" +
            " and academic_calendar_semesters.deleted_at is null\n" +
            "and semesters.status = :status " +
            "and semesters.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countMappedSemestersAgainstAcademicCalendarWithStatus(UUID academicSessionUUID, String name, Boolean status);

    @Query("select count(*) from semesters\n" +
            "join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid \n" +
            " where academic_calendar_semesters.academic_calendar_uuid = :academicCalendarUUID\n" +
            " and semesters.deleted_at is null\n" +
            " and academic_calendar_semesters.deleted_at is null\n" +
            "and semesters.name ILIKE concat('%',:name,'%') ")
    Mono<Long> countMappedSemestersAgainstAcademicCalendarWithOutStatus(UUID academicCalendarUUID, String name);

    @Query("select count(*) FROM semesters\n" +
            " WHERE semesters.uuid NOT IN(\n" +
            " select semesters.uuid from semesters\n" +
            " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
            " join academic_calendars on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
            " where academic_calendars.academic_session_uuid = :academicCalendarUUID\n" +
            " and academic_calendars.course_level_uuid =:courseLevelUUID\n" +
            " and semesters.deleted_at is null\n" +
            " and academic_calendars.deleted_at is null\n" +
            " and academic_calendar_semesters.deleted_at is null)\n" +
            "and semesters.status = :status " +
            " and semesters.name ILIKE concat('%',:name,'%')" +
            " AND semesters.deleted_at IS NULL ")
    Mono<Long> countUnMappedSemestersAgainstAcademicCalendarWithStatus(UUID academicCalendarUUID, UUID courseLevelUUID, String name, Boolean status);

    @Query("select count(*) FROM semesters\n" +
            " WHERE semesters.uuid NOT IN(\n" +
            " select semesters.uuid from semesters\n" +
            " join academic_calendar_semesters on semesters.uuid = academic_calendar_semesters.semester_uuid\n" +
            " join academic_calendars on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
            " where academic_calendars.academic_session_uuid = :academicSessionUUID\n" +
            " and academic_calendars.course_level_uuid =:courseLevelUUID\n" +
            " and semesters.deleted_at is null\n" +
            " and academic_calendars.deleted_at is null\n" +
            " and academic_calendar_semesters.deleted_at is null)\n" +
            " and semesters.name ILIKE concat('%',:name,'%')" +
            " AND semesters.deleted_at IS NULL ")
    Mono<Long> countUnMappedSemestersAgainstAcademicCalendarWithOutStatus(UUID academicSessionUUID, UUID courseLevelUUID, String name);

    @Query("select count(*) from" +
            "(select distinct semesters.* FROM semesters\n" +
            "join enrollments on semesters.uuid = enrollments.semester_uuid\n" +
            "join students on students.uuid = enrollments.student_uuid\n" +
            "join registrations on students.uuid = registrations.student_uuid\n" +
            "where enrollments.course_uuid = :courseUUID\n" +
            "and students.uuid = :studentUUID\n" +
            "and registrations.student_uuid = enrollments.student_uuid\n" +
            "and registrations.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and enrollments.deleted_at is null\n" +
            "and semesters.deleted_at is null\n" +
            "and semesters.status = :status " +
            "and semesters.name ILIKE concat('%',:name,'%')) as semester")
    Mono<Long> countSemestersAgainstCourseWithStatus(String name, UUID courseUUID, UUID studentUUID, Boolean status);

    @Query("select count(*) from" +
            "(select distinct semesters.* FROM semesters\n" +
            "join enrollments on semesters.uuid = enrollments.semester_uuid\n" +
            "join students on students.uuid = enrollments.student_uuid\n" +
            "join registrations on students.uuid = registrations.student_uuid\n" +
            "where enrollments.course_uuid = :courseUUID\n" +
            "and students.uuid = :studentUUID\n" +
            "and registrations.student_uuid = enrollments.student_uuid\n" +
            "and registrations.deleted_at is null\n" +
            "and students.deleted_at is null\n" +
            "and enrollments.deleted_at is null\n" +
            "and semesters.deleted_at is null\n" +
            "and semesters.name ILIKE concat('%',:name,'%')) as semester")
    Mono<Long> countSemestersAgainstCourseWithOutStatus(String name, UUID courseUUID, UUID studentUUID);
}
