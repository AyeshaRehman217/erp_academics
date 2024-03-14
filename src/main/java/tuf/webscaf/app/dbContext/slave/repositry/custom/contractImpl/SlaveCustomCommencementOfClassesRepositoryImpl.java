package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCommencementOfClassesDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentFatherPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCommencementOfClassesRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAilmentMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomClassroomMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCommencementOfClassesMapper;

import java.util.UUID;

public class SlaveCustomCommencementOfClassesRepositoryImpl implements SlaveCustomCommencementOfClassesRepository {
    private DatabaseClient client;
    private SlaveCommencementOfClassesDto slaveCommencementOfClassesDto;

    @Autowired
    public SlaveCustomCommencementOfClassesRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCommencementOfClassesDto> indexWithoutStatus(String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select commencement_of_classes.*, \n" +
                "concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) as key\n" +
                "from commencement_of_classes \n" +
                "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
                "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
                "join students on commencement_of_classes.student_uuid=students.uuid\n" +
                "where commencement_of_classes.deleted_at is null\n" +
                "and academic_sessions.deleted_at is null\n" +
                "and subjects.deleted_at is null\n" +
                "and students.deleted_at is null \n" +
                " AND (concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) ILIKE '%" + key + "%' \n" +
                "  or commencement_of_classes.description ILIKE '%" + description + "%' \n" +
                " ) " +
                "ORDER BY commencement_of_classes." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCommencementOfClassesMapper mapper = new SlaveCustomCommencementOfClassesMapper();

        Flux<SlaveCommencementOfClassesDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCommencementOfClassesDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCommencementOfClassesDto> indexWithStatus(Boolean status, String key, String description, String dp, String d, Integer size, Long page) {
        String query = "select commencement_of_classes.*, \n" +
                " concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) as key \n" +
                " from commencement_of_classes \n" +
                " join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                " join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                " join students on commencement_of_classes.student_uuid=students.uuid \n" +
                " where commencement_of_classes.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and subjects.deleted_at is null \n" +
                " and students.deleted_at is null  \n" +
                " AND commencement_of_classes.status = " + status +
                " AND (concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) ILIKE '%" + key + "%' \n" +
                "  or commencement_of_classes.description ILIKE '%" + description + "%' \n" +
                " ) " +
                "ORDER BY commencement_of_classes." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCommencementOfClassesMapper mapper = new SlaveCustomCommencementOfClassesMapper();

        Flux<SlaveCommencementOfClassesDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCommencementOfClassesDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveCommencementOfClassesDto> showByUuid(UUID uuid) {
        String query = "select commencement_of_classes.*, " +
                "concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) as key" +
                " from commencement_of_classes \n" +
                " join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid \n" +
                " join subjects on commencement_of_classes.subject_uuid=subjects.uuid \n" +
                " join students on commencement_of_classes.student_uuid=students.uuid \n" +
                " where commencement_of_classes.deleted_at is null \n" +
                " and academic_sessions.deleted_at is null \n" +
                " and subjects.deleted_at is null\n" +
                " and students.deleted_at is null\n" +
                " and commencement_of_classes.uuid= '" + uuid +
                "'";

        SlaveCustomCommencementOfClassesMapper mapper = new SlaveCustomCommencementOfClassesMapper();

        Mono<SlaveCommencementOfClassesDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCommencementOfClassesDto))
                .one();

        return result;
    }

}
