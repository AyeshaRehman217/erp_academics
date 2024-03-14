package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailRepository;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarDetailRepository extends ReactiveCrudRepository<SlaveAcademicCalendarDetailEntity, Long>, SlaveCustomAcademicCalendarDetailRepository {

    Mono<SlaveAcademicCalendarDetailEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveAcademicCalendarDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveAcademicCalendarDetailEntity> findAllByCommentsContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String comments);

    Flux<SlaveAcademicCalendarDetailEntity> findAllByCommentsContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String comments, Boolean status);

    Mono<Long> countByCommentsContainingIgnoreCaseAndDeletedAtIsNull(String comments);

    Mono<Long> countByCommentsContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String comments, Boolean status);

    /**
     * Count Number of Academic Calendar Details against Academic Calendar and Academic Session  with or without status Filter
     **/
    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_calendar_details.status = :status \n" +
            " AND academic_sessions.uuid = :academicSessionUUID \n" +
            " AND academic_calendars.uuid = :academicCalendarUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicSessionAndCalendarWithStatus(Boolean status, UUID academicSessionUUID, UUID academicCalendarUUID, String comments, String key);

    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_sessions.uuid = :academicSessionUUID \n" +
            " AND academic_calendars.uuid = :academicCalendarUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicSessionAndCalendarWithoutStatus(UUID academicSessionUUID, UUID academicCalendarUUID, String comments, String key);

    /**
     * Count Number of Academic Calendar Details against Academic Session  with or without status Filter
     **/
    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_calendar_details.status = :status \n" +
            " AND academic_sessions.uuid = :academicSessionUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicSessionWithStatus(Boolean status, UUID academicSessionUUID, String comments, String key);

    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_sessions.uuid = :academicSessionUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicSessionWithoutStatus(UUID academicSessionUUID, String comments, String key);

    /**
     * Count Number of Academic Calendar Details against Academic Calendar  with or without status Filter
     **/
    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_calendar_details.status = :status \n" +
            " AND academic_calendars.uuid = :academicCalendarUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicCalendarWithStatus(Boolean status, UUID academicCalendarUUID, String comments, String key);

    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_calendars.uuid = :academicCalendarUUID \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsAgainstAcademicCalendarWithoutStatus(UUID academicCalendarUUID, String comments, String key);

    /**
     * Count Number of Academic Calendar Details with or without status Filter
     **/
    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND academic_calendar_details.status = :status \n" +
            " AND (academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsWithStatus(Boolean status, String comments, String key);

    @Query("SELECT count(*) FROM academic_calendar_details \n" +
            " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
            " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
            " where academic_calendar_details.deleted_at is null \n" +
            " and academic_calendars.deleted_at is null \n" +
            " and academic_sessions.deleted_at is null \n" +
            " AND ( academic_calendar_details.comments ILIKE concat('%',:comments,'%') \n" +
            " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countDetailsWithoutStatus(String comments, String key);
}
