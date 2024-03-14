package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSpouseAilmentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAilmentMapper;

import java.util.UUID;

public class SlaveCustomTeacherSpouseAilmentPvtRepositoryImpl implements SlaveCustomTeacherSpouseAilmentPvtRepository {
    private DatabaseClient client;
    private SlaveAilmentEntity slaveAilmentEntity;

    @Autowired
    public SlaveCustomTeacherSpouseAilmentPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAilmentEntity> existingTeacherSpouseAilmentsList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT ailments.* FROM ailments\n" +
                "WHERE ailments.uuid NOT IN(\n" +
                "SELECT ailments.uuid FROM ailments\n" +
                "LEFT JOIN teacher_spouse_ailments_pvt \n" +
                "ON teacher_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
                "WHERE teacher_spouse_ailments_pvt.teacher_spouse_uuid = '" + teacherSpouseUUID +
                "' AND teacher_spouse_ailments_pvt.deleted_at IS NULL \n" +
                "AND ailments.deleted_at IS NULL)\n" +
                "AND (ailments.name ILIKE '%" + name + "%'" +
                " or ailments.description ILIKE '%" + description + "%')" +
                "AND ailments.deleted_at IS NULL " +
                "ORDER BY ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAilmentEntity> existingTeacherSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT ailments.* FROM ailments\n" +
                "WHERE ailments.uuid NOT IN(\n" +
                "SELECT ailments.uuid FROM ailments\n" +
                "LEFT JOIN teacher_spouse_ailments_pvt \n" +
                "ON teacher_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
                "WHERE teacher_spouse_ailments_pvt.teacher_spouse_uuid = '" + teacherSpouseUUID +
                "' AND teacher_spouse_ailments_pvt.deleted_at IS NULL \n" +
                "AND ailments.deleted_at IS NULL)\n" +
                "AND (ailments.name ILIKE '%" + name + "%' " +
                "or ailments.description ILIKE '%" + description + "%')" +
                "AND ailments.deleted_at IS NULL " +
                "AND ailments.status = " + status +
                " ORDER BY ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAilmentEntity> showTeacherSpouseAilmentsList(UUID teacherSpouseUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select ailments.* from ailments\n" +
                "left join teacher_spouse_ailments_pvt \n" +
                "on ailments.uuid = teacher_spouse_ailments_pvt.ailment_uuid\n" +
                "where teacher_spouse_ailments_pvt.teacher_spouse_uuid = '" + teacherSpouseUUID +
                "' and ailments.deleted_at is null\n" +
                "and teacher_spouse_ailments_pvt.deleted_at is null\n" +
                " and (ailments.name ILIKE  '%" + name + "%' or ailments.description ILIKE  '%" + description + "%') " +
                "order by ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAilmentEntity> showTeacherSpouseAilmentsListWithStatus(UUID teacherSpouseUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select ailments.* from ailments\n" +
                "left join teacher_spouse_ailments_pvt \n" +
                "on ailments.uuid = teacher_spouse_ailments_pvt.ailment_uuid\n" +
                "where teacher_spouse_ailments_pvt.teacher_spouse_uuid = '" + teacherSpouseUUID +
                "' and ailments.deleted_at is null\n" +
                "and teacher_spouse_ailments_pvt.deleted_at is null\n" +
                "and ailments.status = " + status +
                " and (ailments.name ILIKE  '%" + name + "%' or ailments.description ILIKE  '%" + description + "%') " +
                "order by ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }
}
