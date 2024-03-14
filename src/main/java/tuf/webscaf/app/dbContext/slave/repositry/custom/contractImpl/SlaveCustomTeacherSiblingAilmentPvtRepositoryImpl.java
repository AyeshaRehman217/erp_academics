package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSiblingAilmentPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomAilmentMapper;

import java.util.UUID;

public class SlaveCustomTeacherSiblingAilmentPvtRepositoryImpl implements SlaveCustomTeacherSiblingAilmentPvtRepository {
    private DatabaseClient client;
    private SlaveAilmentEntity slaveAilmentEntity;

    @Autowired
    public SlaveCustomTeacherSiblingAilmentPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveAilmentEntity> existingTeacherSiblingAilmentsList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT ailments.* FROM ailments\n" +
                "WHERE ailments.uuid NOT IN(\n" +
                "SELECT ailments.uuid FROM ailments\n" +
                "LEFT JOIN teacher_sibling_ailments_pvt\n" +
                "ON teacher_sibling_ailments_pvt.ailment_uuid = ailments.uuid\n" +
                "WHERE teacher_sibling_ailments_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' AND teacher_sibling_ailments_pvt.deleted_at IS NULL\n" +
                "AND ailments.deleted_at IS NULL)\n" +
                "AND (ailments.name ILIKE '%" + name + "%' " +
                "or ailments.description ILIKE '%" + description + "%' )" +
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
    public Flux<SlaveAilmentEntity> existingTeacherSiblingAilmentsListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT ailments.* FROM ailments\n" +
                "WHERE ailments.uuid NOT IN(\n" +
                "SELECT ailments.uuid FROM ailments\n" +
                "LEFT JOIN teacher_sibling_ailments_pvt\n" +
                "ON teacher_sibling_ailments_pvt.ailment_uuid = ailments.uuid\n" +
                "WHERE teacher_sibling_ailments_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' AND teacher_sibling_ailments_pvt.deleted_at IS NULL\n" +
                "AND ailments.deleted_at IS NULL)\n" +
                "AND (ailments.name ILIKE '%" + name + "%' " +
                "or ailments.description ILIKE '%" + description + "%' )" +
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
    public Flux<SlaveAilmentEntity> showTeacherSiblingAilmentsList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select ailments.* from ailments\n" +
                "left join teacher_sibling_ailments_pvt \n" +
                "on ailments.uuid = teacher_sibling_ailments_pvt.ailment_uuid\n" +
                "where teacher_sibling_ailments_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' and ailments.deleted_at is null\n" +
                "and teacher_sibling_ailments_pvt.deleted_at is null\n" +
                "AND (ailments.name ILIKE '%" + name + "%' " +
                "or ailments.description ILIKE '%" + description + "%' )" +
                "order by ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveAilmentEntity> showTeacherSiblingAilmentsListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select ailments.* from ailments\n" +
                "left join teacher_sibling_ailments_pvt \n" +
                "on ailments.uuid = teacher_sibling_ailments_pvt.ailment_uuid\n" +
                "where teacher_sibling_ailments_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' and ailments.deleted_at is null\n" +
                "and teacher_sibling_ailments_pvt.deleted_at is null\n" +
                "and ailments.status = "+ status +
                " and (ailments.name ILIKE '%" + name + "%' " +
                "or ailments.description ILIKE '%" + description + "%' )" +
                "order by ailments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomAilmentMapper mapper = new SlaveCustomAilmentMapper();

        Flux<SlaveAilmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveAilmentEntity))
                .all();

        return result;
    }
}
