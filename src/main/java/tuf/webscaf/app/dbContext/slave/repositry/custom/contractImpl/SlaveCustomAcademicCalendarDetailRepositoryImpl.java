package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAcademicCalendarDetailDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailHolidayPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicCalendarDetailRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAcademicCalendarDetailMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomHolidayMapper;

import java.util.UUID;

public class SlaveCustomAcademicCalendarDetailRepositoryImpl implements SlaveCustomAcademicCalendarDetailRepository {
    private DatabaseClient client;
    private SlaveAcademicCalendarDetailDto slaveAcademicCalendarDetailDto;

    @Autowired
    public SlaveCustomAcademicCalendarDetailRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarAndSessionWithStatus(UUID academicCalendarUUID, UUID academicSessionUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_calendar_details.status = " + status +
                " AND academic_sessions.uuid = '" + academicSessionUUID +
                "' AND academic_calendars.uuid = '" + academicCalendarUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarAndSessionWithoutStatus(UUID academicCalendarUUID, UUID academicSessionUUID, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_sessions.uuid = '" + academicSessionUUID +
                "' AND academic_calendars.uuid = '" + academicCalendarUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarWithStatus(UUID academicCalendarUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_calendar_details.status = " + status +
                " AND academic_calendars.uuid = '" + academicCalendarUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstCalendarWithoutStatus(UUID academicCalendarUUID, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_calendars.uuid = '" + academicCalendarUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstSessionWithStatus(UUID academicSessionUUID, Boolean status, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_calendar_details.status = " + status +
                " AND academic_sessions.uuid = '" + academicSessionUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexAgainstSessionWithoutStatus(UUID academicSessionUUID, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_sessions.uuid = '" + academicSessionUUID +
                "' AND ( academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexWithStatus(Boolean status, String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND academic_calendar_details.status = " + status +
                " AND (academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicCalendarDetailDto> indexWithoutStatus(String key, String comments, String dp, String d, Integer size, Long page) {
        String query = "select academic_calendar_details.*," +
                " concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) as key " +
                " from academic_calendar_details " +
                " join academic_calendars on academic_calendar_details.academic_calendar_uuid=academic_calendars.uuid \n" +
                " join academic_sessions on academic_calendars.academic_session_uuid=academic_sessions.uuid \n" +
                " where academic_calendar_details.deleted_at is null" +
                " and academic_calendars.deleted_at is null " +
                " and academic_sessions.deleted_at is null " +
                " AND (academic_calendar_details.comments ILIKE '%" + comments + "%'" +
                " or concat_ws('|',academic_calendar_details.calendar_date,academic_calendars.name,academic_sessions.name) ILIKE '%" + key + "%' )" +
                " ORDER BY academic_calendar_details." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicCalendarDetailMapper mapper = new SlaveCustomAcademicCalendarDetailMapper();

        Flux<SlaveAcademicCalendarDetailDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicCalendarDetailDto))
                .all();

        return result;
    }
}
