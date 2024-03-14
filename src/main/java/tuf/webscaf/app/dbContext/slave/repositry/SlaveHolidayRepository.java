package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailHolidayPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveHolidayRepository extends ReactiveCrudRepository<SlaveHolidayEntity, Long>, SlaveCustomAcademicCalendarDetailHolidayPvtRepository {

    Mono<SlaveHolidayEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveHolidayEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, Pageable pageable);

    Flux<SlaveHolidayEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    
    /**
     * Count Mapped Holidays against Academic Calendar Detail with or without status filter
     **/

    // query used for count of mapped holidays records for given academic calendar detail
    @Query("select count(*) from holidays\n" +
            "left join academic_calendar_detail_holidays_pvt\n" +
            "on holidays.uuid = academic_calendar_detail_holidays_pvt.holiday_uuid\n" +
            "where academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "and holidays.deleted_at is null\n" +
            "and academic_calendar_detail_holidays_pvt.deleted_at is null\n" +
            "and (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedAcademicCalendarDetailHolidays(UUID academicCalendarDetailUUID, String name, String description);

    // query used for count of mapped holidays records for given academic calendar detail
    @Query("select count(*) from holidays\n" +
            "left join academic_calendar_detail_holidays_pvt\n" +
            "on holidays.uuid = academic_calendar_detail_holidays_pvt.holiday_uuid\n" +
            "where academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "and holidays.deleted_at is null\n" +
            "and holidays.status = :status " +
            "and academic_calendar_detail_holidays_pvt.deleted_at is null\n" +
            "and (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedAcademicCalendarDetailHolidaysWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status);

    
    /**
     * Count Unmapped Holidays against Academic Calendar Detail with or without status filter
     **/
    
    //query used in pvt Academic Calendar Detail Holidays Pvt
    @Query("SELECT count(*) FROM holidays \n" +
            "WHERE holidays.uuid NOT IN(\n" +
            "SELECT holidays.uuid FROM holidays\n" +
            "LEFT JOIN academic_calendar_detail_holidays_pvt\n" +
            "ON academic_calendar_detail_holidays_pvt.holiday_uuid = holidays.uuid \n" +
            "WHERE academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "AND academic_calendar_detail_holidays_pvt.deleted_at IS NULL\n" +
            "AND holidays.deleted_at IS NULL )\n" +
            "AND holidays.deleted_at IS NULL " +
            "AND (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingAcademicCalendarDetailHolidaysRecords(UUID academicCalendarDetailUUID, String name, String description);

    //query used in pvt Academic Calendar Detail Holidays Pvt With Status Filter
    @Query("SELECT count(*) FROM holidays \n" +
            "WHERE holidays.uuid NOT IN(\n" +
            "SELECT holidays.uuid FROM holidays\n" +
            "LEFT JOIN academic_calendar_detail_holidays_pvt\n" +
            "ON academic_calendar_detail_holidays_pvt.holiday_uuid = holidays.uuid \n" +
            "WHERE academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = :academicCalendarDetailUUID\n" +
            "AND academic_calendar_detail_holidays_pvt.deleted_at IS NULL\n" +
            "AND holidays.deleted_at IS NULL )\n" +
            "AND holidays.deleted_at IS NULL " +
            "AND holidays.status = :status \n" +
            "AND (holidays.name ILIKE concat('%',:name,'%') " +
            "or holidays.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingAcademicCalendarDetailHolidaysRecordsWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status);
}
