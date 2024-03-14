package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicSessionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAcademicSessionRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAcademicSessionMapper;

import java.util.UUID;


public class SlaveCustomAcademicSessionRepositoryImpl implements SlaveCustomAcademicSessionRepository {
    private DatabaseClient client;
    private SlaveAcademicSessionEntity slaveAcademicSessionEntity;

    @Autowired
    public SlaveCustomAcademicSessionRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAcademicSessionEntity> showAcademicSessionOfCalendarWithoutStatus(String name, String dp, String d, Integer size, Long page) {
        String query = "select academic_sessions.* from academic_sessions \n" +
                "  join academic_calendars on academic_sessions.uuid = academic_calendars.academic_session_uuid \n" +
                "  where academic_sessions.deleted_at IS NULL\n" +
                "  AND academic_calendars.deleted_at IS NULL\n" +
                "  AND academic_sessions.name ILIKE '%" + name + "%' " +
                " ORDER BY academic_sessions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicSessionMapper mapper = new SlaveCustomAcademicSessionMapper();

        Flux<SlaveAcademicSessionEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicSessionEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicSessionEntity> showAcademicSessionOfCalendarWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select academic_sessions.* from academic_sessions \n" +
                "  join academic_calendars on academic_sessions.uuid = academic_calendars.academic_session_uuid \n" +
                "  where academic_sessions.deleted_at IS NULL\n" +
                "  AND academic_calendars.deleted_at IS NULL\n" +
                "  AND  academic_sessions.status = " + status +
                "  AND academic_sessions.name ILIKE '%" + name + "%' " +
                " ORDER BY academic_sessions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicSessionMapper mapper = new SlaveCustomAcademicSessionMapper();

        Flux<SlaveAcademicSessionEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicSessionEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicSessionEntity> showAcademicSessionOfTeacherWithoutStatus(UUID teacherUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select distinct academic_sessions.* from academic_sessions \n" +
                "  join teacher_subjects on academic_sessions.uuid = teacher_subjects.academic_session_uuid \n" +
                "  where academic_sessions.deleted_at IS NULL\n" +
                "  AND teacher_subjects.deleted_at IS NULL\n" +
                "  AND teacher_subjects.teacher_uuid = '" + teacherUUID +
                "'  AND academic_sessions.name ILIKE '%" + name + "%' " +
                " ORDER BY academic_sessions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicSessionMapper mapper = new SlaveCustomAcademicSessionMapper();

        Flux<SlaveAcademicSessionEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicSessionEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAcademicSessionEntity> showAcademicSessionOfTeacherWithStatus(UUID teacherUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select distinct academic_sessions.* from academic_sessions \n" +
                "  join teacher_subjects on academic_sessions.uuid = teacher_subjects.academic_session_uuid \n" +
                "  where academic_sessions.deleted_at IS NULL\n" +
                "  AND  academic_sessions.status = " + status +
                "  AND teacher_subjects.teacher_uuid = '" + teacherUUID +
                "'  AND teacher_subjects.deleted_at IS NULL\n" +
                "  AND academic_sessions.name ILIKE '%" + name + "%' " +
                " ORDER BY academic_sessions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAcademicSessionMapper mapper = new SlaveCustomAcademicSessionMapper();

        Flux<SlaveAcademicSessionEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAcademicSessionEntity))
                .all();

        return result;
    }


}
