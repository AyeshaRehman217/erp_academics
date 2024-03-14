package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarSemesterEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarSemesterRepository extends ReactiveCrudRepository<AcademicCalendarSemesterEntity, Long> {
    Mono<AcademicCalendarSemesterEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarSemesterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarSemesterEntity> findFirstByAcademicCalendarUUIDAndSemesterUUIDAndDeletedAtIsNull(UUID academicCalendarUUID, UUID semesterUUID);

    Flux<AcademicCalendarSemesterEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);

    Flux<AcademicCalendarSemesterEntity> findAllByAcademicCalendarUUIDAndDeletedAtIsNull(UUID academicCalendarUUID);

    Flux<AcademicCalendarSemesterEntity> findAllByAcademicCalendarUUIDAndSemesterUUIDInAndDeletedAtIsNull(UUID academicCalendarUUID, List<UUID> semesterUUID);
//    Flux<AcademicCalendarSemesterEntity> findAllByAcademicCalendarUUIDAndDeletedAtIsNull(UUID academicCalendarUUIDList);

    @Query("select academic_calendars.uuid,academic_calendar_semesters.uuid \n" +
            "from academic_calendars \n" +
            "join course_levels on course_levels.uuid=academic_calendars.course_level_uuid \n" +
            "join academic_calendar_semesters on academic_calendar_semesters.academic_calendar_uuid=academic_calendars.uuid \n" +
            "where academic_calendar_semesters.semester_uuid in (:semester) \n" +
            "and course_levels.uuid= :courseLevelUUID \n" +
            "and academic_calendars.uuid= :academicCalendarUUID \n" +
            "and academic_calendars.deleted_at is null \n" +
            "and academic_calendar_semesters.deleted_at is null" +
            " and course_levels.deleted_at is null fetch first row only")
    Mono<AcademicCalendarSemesterEntity> checkIfSemesterIsUniqueAgainstCalendarAndCourseLevel(List<UUID> semester, UUID courseLevelUUID, UUID academicCalendarUUID);
}
