 package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEventEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailEventPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAcademicCalendarEventMapper;

import java.util.UUID;

public class SlaveCustomAcademicCalendarDetailEventPvtRepositoryImpl implements SlaveCustomAcademicCalendarDetailEventPvtRepository {
    private DatabaseClient client;
    private SlaveAcademicCalendarEventEntity slaveEventEntity;

    @Autowired
    public SlaveCustomAcademicCalendarDetailEventPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAcademicCalendarEventEntity> unmappedAcademicCalendarDetailEventsList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT academic_calendar_events.* FROM academic_calendar_events\n" +
                "WHERE academic_calendar_events.uuid NOT IN(\n" +
                "SELECT academic_calendar_events.uuid FROM academic_calendar_events\n" +
                "LEFT JOIN academic_calendar_detail_events_pvt \n" +
                "ON academic_calendar_detail_events_pvt.academic_calendar_event_uuid = academic_calendar_events.uuid \n" +
                "WHERE academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' AND academic_calendar_detail_events_pvt.deleted_at IS NULL \n" +
                "AND academic_calendar_events.deleted_at IS NULL)\n" +
                "AND (academic_calendar_events.name ILIKE '%" + name + "%'" +
                " or academic_calendar_events.description ILIKE '%" + description + "%')" +
                "AND academic_calendar_events.deleted_at IS NULL " +
                "ORDER BY academic_calendar_events." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarEventMapper mapper = new SlaveCustomAcademicCalendarEventMapper();

        Flux<SlaveAcademicCalendarEventEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEventEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarEventEntity> unmappedAcademicCalendarDetailEventsListWithStatus(UUID academicCalendarDetailUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT academic_calendar_events.* FROM academic_calendar_events\n" +
                "WHERE academic_calendar_events.uuid NOT IN(\n" +
                "SELECT academic_calendar_events.uuid FROM academic_calendar_events\n" +
                "LEFT JOIN academic_calendar_detail_events_pvt \n" +
                "ON academic_calendar_detail_events_pvt.academic_calendar_event_uuid = academic_calendar_events.uuid \n" +
                "WHERE academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' AND academic_calendar_detail_events_pvt.deleted_at IS NULL \n" +
                "AND academic_calendar_events.deleted_at IS NULL)\n" +
                "AND (academic_calendar_events.name ILIKE '%" + name + "%' " +
                "or academic_calendar_events.description ILIKE '%" + description + "%')" +
                "AND academic_calendar_events.deleted_at IS NULL " +
                "AND academic_calendar_events.status = " + status +
                " ORDER BY academic_calendar_events." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarEventMapper mapper = new SlaveCustomAcademicCalendarEventMapper();

        Flux<SlaveAcademicCalendarEventEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEventEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarEventEntity> showAcademicCalendarDetailEventsList(UUID academicCalendarDetailUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_events.* from academic_calendar_events\n" +
                "left join academic_calendar_detail_events_pvt \n" +
                "on academic_calendar_events.uuid = academic_calendar_detail_events_pvt.academic_calendar_event_uuid\n" +
                "where academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' and academic_calendar_events.deleted_at is null\n" +
                "and academic_calendar_detail_events_pvt.deleted_at is null\n" +
                " and (academic_calendar_events.name ILIKE  '%" + name + "%' " +
                "or academic_calendar_events.description ILIKE  '%" + description + "%') " +
                "order by academic_calendar_events." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarEventMapper mapper = new SlaveCustomAcademicCalendarEventMapper();

        Flux<SlaveAcademicCalendarEventEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEventEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarEventEntity> showAcademicCalendarDetailEventsListWithStatus(UUID academicCalendarDetailUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_events.* from academic_calendar_events\n" +
                "left join academic_calendar_detail_events_pvt \n" +
                "on academic_calendar_events.uuid = academic_calendar_detail_events_pvt.academic_calendar_event_uuid\n" +
                "where academic_calendar_detail_events_pvt.academic_calendar_detail_uuid = '" + academicCalendarDetailUUID +
                "' and academic_calendar_events.deleted_at is null\n" +
                "and academic_calendar_detail_events_pvt.deleted_at is null\n" +
                "and academic_calendar_events.status = " + status +
                " and (academic_calendar_events.name ILIKE  '%" + name + "%' " +
                "or academic_calendar_events.description ILIKE  '%" + description + "%') " +
                "order by academic_calendar_events." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarEventMapper mapper = new SlaveCustomAcademicCalendarEventMapper();

        Flux<SlaveAcademicCalendarEventEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveEventEntity))
                .all();

        return result;
    }
}
