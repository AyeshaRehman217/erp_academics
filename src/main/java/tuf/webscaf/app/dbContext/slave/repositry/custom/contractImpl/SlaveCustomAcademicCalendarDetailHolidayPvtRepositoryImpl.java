package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailHolidayPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomHolidayMapper;

import java.util.UUID;

public class SlaveCustomAcademicCalendarDetailHolidayPvtRepositoryImpl implements SlaveCustomAcademicCalendarDetailHolidayPvtRepository {
    private DatabaseClient client;
    private SlaveHolidayEntity slaveHolidayEntity;

    @Autowired
    public SlaveCustomAcademicCalendarDetailHolidayPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveHolidayEntity> unmappedAcademicCalendarDetailHolidaysList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT holidays.* FROM holidays\n" +
                "WHERE holidays.uuid NOT IN(\n" +
                "SELECT holidays.uuid FROM holidays\n" +
                "LEFT JOIN academic_calendar_detail_holidays_pvt \n" +
                "ON academic_calendar_detail_holidays_pvt.holiday_uuid = holidays.uuid \n" +
                "WHERE academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' AND academic_calendar_detail_holidays_pvt.deleted_at IS NULL \n" +
                "AND holidays.deleted_at IS NULL)\n" +
                "AND (holidays.name ILIKE '%" + name + "%'" +
                " or holidays.description ILIKE '%" + description + "%')" +
                "AND holidays.deleted_at IS NULL " +
                "ORDER BY holidays." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHolidayMapper mapper = new SlaveCustomHolidayMapper();

        Flux<SlaveHolidayEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHolidayEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHolidayEntity> unmappedAcademicCalendarDetailHolidaysListWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT holidays.* FROM holidays\n" +
                "WHERE holidays.uuid NOT IN(\n" +
                "SELECT holidays.uuid FROM holidays\n" +
                "LEFT JOIN academic_calendar_detail_holidays_pvt \n" +
                "ON academic_calendar_detail_holidays_pvt.holiday_uuid = holidays.uuid \n" +
                "WHERE academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' AND academic_calendar_detail_holidays_pvt.deleted_at IS NULL \n" +
                "AND holidays.deleted_at IS NULL)\n" +
                "AND (holidays.name ILIKE '%" + name + "%' " +
                "or holidays.description ILIKE '%" + description + "%')" +
                "AND holidays.deleted_at IS NULL " +
                "AND holidays.status = " + status +
                " ORDER BY holidays." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHolidayMapper mapper = new SlaveCustomHolidayMapper();

        Flux<SlaveHolidayEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHolidayEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHolidayEntity> showAcademicCalendarDetailHolidaysList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select holidays.* from holidays\n" +
                "left join academic_calendar_detail_holidays_pvt \n" +
                "on holidays.uuid = academic_calendar_detail_holidays_pvt.holiday_uuid\n" +
                "where academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' and holidays.deleted_at is null\n" +
                "and academic_calendar_detail_holidays_pvt.deleted_at is null\n" +
                " and (holidays.name ILIKE  '%" + name + "%' " +
                "or holidays.description ILIKE  '%" + description + "%') " +
                "order by holidays." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHolidayMapper mapper = new SlaveCustomHolidayMapper();

        Flux<SlaveHolidayEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHolidayEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHolidayEntity> showAcademicCalendarDetailHolidaysListWithStatus(UUID academicCalendarDetailUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select holidays.* from holidays\n" +
                "left join academic_calendar_detail_holidays_pvt \n" +
                "on holidays.uuid = academic_calendar_detail_holidays_pvt.holiday_uuid\n" +
                "where academic_calendar_detail_holidays_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' and holidays.deleted_at is null\n" +
                "and academic_calendar_detail_holidays_pvt.deleted_at is null\n" +
                "and holidays.status = " + status +
                " and (holidays.name ILIKE  '%" + name + "%' " +
                "or holidays.description ILIKE  '%" + description + "%') " +
                "order by holidays." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHolidayMapper mapper = new SlaveCustomHolidayMapper();

        Flux<SlaveHolidayEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHolidayEntity))
                .all();

        return result;
    }
}
