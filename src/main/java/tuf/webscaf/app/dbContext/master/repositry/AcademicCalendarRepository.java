package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarRepository extends ReactiveCrudRepository<AcademicCalendarEntity, Long> {

    Mono<AcademicCalendarEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarEntity> findFirstByAcademicSessionUUIDAndCourseLevelUUIDAndDeletedAtIsNull(UUID academicSession, UUID courseLevelUUID);

    Mono<AcademicCalendarEntity> findFirstByAcademicSessionUUIDAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(UUID academicSession, UUID courseLevelUUID, UUID uuid);

    Mono<AcademicCalendarEntity> findFirstByAcademicSessionUUIDAndDeletedAtIsNull(UUID academicSession);

    Mono<AcademicCalendarEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AcademicCalendarEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<AcademicCalendarEntity> findByStatusAndDeletedAtIsNull(Boolean status);

    @Query("SELECT academic_session_uuid, course_level_uuid , semester_uuid from academic_calendars\n" +
            "join academic_calendar_semesters on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
            "where  academic_calendar_semesters.deleted_at is null\n" +
            "and academic_calendars.deleted_at is null\n" +
            "and academic_calendars.academic_session_uuid = :academicSessionUUID\n" +
            "and academic_calendars.course_level_uuid = :courseLevelUUID\n" +
            "and academic_calendar_semesters.semester_uuid IN (:semesterUUID) \n" +
            "fetch first row only")
    Mono<AcademicCalendarEntity> checkAcademicCalendarIsUnique(UUID academicSessionUUID, UUID courseLevelUUID, List<UUID> semesterUUID);

//    Flux<AcademicCalendarEntity> findAllByAcademicSessionUUIDAndSemesterUUIDInAndDeletedAtIsNull(UUID academicSessionUUID, List<UUID> semesterUUID);

    @Query("SELECT academic_session_uuid, course_level_uuid , semester_uuid from academic_calendars\n" +
            "join academic_calendar_semesters on academic_calendars.uuid = academic_calendar_semesters.academic_calendar_uuid\n" +
            "where  academic_calendar_semesters.deleted_at is null\n" +
            "and academic_calendars.deleted_at is null\n" +
            "and academic_calendars.academic_session_uuid = :academicSessionUUID\n" +
            "and academic_calendars.course_level_uuid = :courseLevelUUID\n" +
            "and academic_calendar_semesters.semester_uuid IN (:semesterUUID) " +
            "and academic_calendars.uuid != :academicCalendarUUID \n" +
            "fetch first row only")
    Mono<AcademicCalendarEntity> checkAcademicCalendarIsUniqueAndAcademicCalendarIsNot(UUID academicSessionUUID, UUID courseLevelUUID, List<UUID> semesterUUID,UUID academicCalendarUUID);

}
