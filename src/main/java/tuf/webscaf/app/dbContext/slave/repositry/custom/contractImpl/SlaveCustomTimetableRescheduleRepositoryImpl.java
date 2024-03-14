package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTimetableCreationDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTimetableCreationRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTimetableRescheduleRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTimetableCreationMapper;

import java.util.UUID;

public class SlaveCustomTimetableRescheduleRepositoryImpl implements SlaveCustomTimetableRescheduleRepository {
    private DatabaseClient client;
    private SlaveTimetableCreationDto slaveTimetableCreationDto;

    @Autowired
    public SlaveCustomTimetableRescheduleRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveTimetableCreationDto> indexWithoutStatus(String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select timetableView.*, concat_ws('|',academic_sessions.name,subjects.name) as key \n" +
                "from timetableView \n" +
                "join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on timetableView.subject_uuid=subjects.uuid \n" +
                "where timetableView.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                "and timetableview.is_rescheduled is true \n" +
                " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE  '%" + key + "%' or timetableView.description ILIKE  '%" + description + "%')" +
                "AND timetableView.deleted_at IS NULL " +
                "ORDER BY timetableView." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTimetableCreationMapper mapper = new SlaveCustomTimetableCreationMapper();

        Flux<SlaveTimetableCreationDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTimetableCreationDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTimetableCreationDto> indexWithStatus(Boolean status, String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select timetableView.*, concat_ws('|',academic_sessions.name,subjects.name) as key \n" +
                "from timetableView \n" +
                "join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on timetableView.subject_uuid=subjects.uuid \n" +
                "where timetableView.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                " and timetableView.status =" + status +
                " and timetableview.is_rescheduled is true \n" +
                " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE  '%" + key + "%' or timetableView.description ILIKE  '%" + description + "%')" +
                "AND timetableView.deleted_at IS NULL " +
                "ORDER BY timetableView." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTimetableCreationMapper mapper = new SlaveCustomTimetableCreationMapper();

        Flux<SlaveTimetableCreationDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTimetableCreationDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTimetableCreationDto> indexWithoutStatusAgainstSubject(UUID subjectUUID, String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select timetableView.*, concat_ws('|',academic_sessions.name,subjects.name) as key \n" +
                "from timetableView \n" +
                "join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on timetableView.subject_uuid=subjects.uuid \n" +
                "where timetableView.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                " and timetableview.is_rescheduled is true \n" +
                " and subjects.uuid = '" + subjectUUID +
                "' AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE  '%" + key + "%' or timetableView.description ILIKE  '%" + description + "%')" +
                "AND timetableView.deleted_at IS NULL " +
                "ORDER BY timetableView." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTimetableCreationMapper mapper = new SlaveCustomTimetableCreationMapper();

        Flux<SlaveTimetableCreationDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTimetableCreationDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveTimetableCreationDto> indexWithStatusAgainstSubject(UUID subjectUUID, Boolean status, String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select timetableView.*, concat_ws('|',academic_sessions.name,subjects.name) as key \n" +
                "from timetableView \n" +
                "join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
                "join subjects on timetableView.subject_uuid=subjects.uuid \n" +
                "where timetableView.deleted_at is null \n" +
                "and academic_sessions.deleted_at is null \n" +
                "and subjects.deleted_at is null \n" +
                " and timetableview.is_rescheduled is true \n" +
                "and subjects.uuid = '" + subjectUUID +
                "' WHERE timetableView.status=" + status +
                " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE  '%" + key + "%' or timetableView.description ILIKE  '%" + description + "%')" +
                "AND timetableView.deleted_at IS NULL " +
                "ORDER BY timetableView." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomTimetableCreationMapper mapper = new SlaveCustomTimetableCreationMapper();

        Flux<SlaveTimetableCreationDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveTimetableCreationDto))
                .all();

        return result;
    }

}
