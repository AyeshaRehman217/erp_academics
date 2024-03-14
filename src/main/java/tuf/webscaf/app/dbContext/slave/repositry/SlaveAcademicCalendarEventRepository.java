package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailEventPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarEventRepository extends ReactiveCrudRepository<SlaveAcademicCalendarEventEntity, Long>, SlaveCustomAcademicCalendarDetailEventPvtRepository {
    Mono<SlaveAcademicCalendarEventEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveAcademicCalendarEventEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveAcademicCalendarEventEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);


    /**
     * Count Mapped Events against Academic Calendar Detail with or without status filter
     **/

    // query used for count of mapped academic calendar events records for given academic calendar detail
    @Query("select count(*) from holidays\n" +
            "left join academic_calendar_detail_events_pvt\n" +
            "on holidays.uuid = academic_calendar_detail_events_pvt.academic_calendar_event_uuid\n" +
            "where academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "and holidays.deleted_at is null\n" +
            "and academic_calendar_detail_events_pvt.deleted_at is null\n" +
            "and (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedAcademicCalendarDetailEvents(UUID academicCalendarDetailUUID, String name, String description);

    // query used for count of mapped academic calendar events records for given academic calendar detail
    @Query("select count(*) from holidays\n" +
            "left join academic_calendar_detail_events_pvt\n" +
            "on holidays.uuid = academic_calendar_detail_events_pvt.academic_calendar_event_uuid\n" +
            "where academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "and holidays.deleted_at is null\n" +
            "and holidays.status = :status " +
            "and academic_calendar_detail_events_pvt.deleted_at is null\n" +
            "and (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedAcademicCalendarDetailEventsWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status);


    /**
     * Count Unmapped Events against Academic Calendar Detail with or without status filter
     **/

    //query used in pvt Academic Calendar Detail Events Pvt
    @Query("SELECT count(*) FROM holidays \n" +
            "WHERE holidays.uuid NOT IN(\n" +
            "SELECT holidays.uuid FROM holidays\n" +
            "LEFT JOIN academic_calendar_detail_events_pvt\n" +
            "ON academic_calendar_detail_events_pvt.academic_calendar_event_uuid = holidays.uuid \n" +
            "WHERE academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "AND academic_calendar_detail_events_pvt.deleted_at IS NULL\n" +
            "AND holidays.deleted_at IS NULL )\n" +
            "AND holidays.deleted_at IS NULL " +
            "AND (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingAcademicCalendarDetailEventsRecords(UUID academicCalendarDetailUUID, String name, String description);

    //query used in pvt Academic Calendar Detail Events Pvt With Status Filter
    @Query("SELECT count(*) FROM holidays \n" +
            "WHERE holidays.uuid NOT IN(\n" +
            "SELECT holidays.uuid FROM holidays\n" +
            "LEFT JOIN academic_calendar_detail_events_pvt\n" +
            "ON academic_calendar_detail_events_pvt.academic_calendar_event_uuid = holidays.uuid \n" +
            "WHERE academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "AND academic_calendar_detail_events_pvt.deleted_at IS NULL\n" +
            "AND holidays.deleted_at IS NULL )\n" +
            "AND holidays.deleted_at IS NULL " +
            "AND holidays.status = :status \n" +
            "AND (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingAcademicCalendarDetailEventsRecordsWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status);
}
