package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherChildNationalityPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomNationalityMapper;

import java.util.UUID;

public class SlaveCustomTeacherChildNationalityPvtRepositoryImpl implements SlaveCustomTeacherChildNationalityPvtRepository {
    private DatabaseClient client;
    private SlaveNationalityEntity slaveNationalityEntity;

    @Autowired
    public SlaveCustomTeacherChildNationalityPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveNationalityEntity> existingTeacherChildNationalitiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN teacher_child_nationalities_pvt\n" +
                "ON teacher_child_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE teacher_child_nationalities_pvt.teacher_child_uuid = '" + teacherChildUUID +
                "' AND teacher_child_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                "AND (nationalities.name ILIKE '%" + name + "%'" +
                "OR nationalities.description ILIKE '%" + description + "%')" +
                "AND nationalities.deleted_at IS NULL " +
                "ORDER BY nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveNationalityEntity> existingTeacherChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN teacher_child_nationalities_pvt\n" +
                "ON teacher_child_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE teacher_child_nationalities_pvt.teacher_child_uuid = '" + teacherChildUUID +
                "' AND teacher_child_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                "AND (nationalities.name ILIKE '%" + name + "%'" +
                "OR nationalities.description ILIKE '%" + description + "%')" +
                "AND nationalities.deleted_at IS NULL " +
                "AND nationalities.status = " + status +
                " ORDER BY nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveNationalityEntity> showTeacherChildNationalitiesList(UUID teacherChildUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join teacher_child_nationalities_pvt \n" +
                "on nationalities.uuid = teacher_child_nationalities_pvt.nationality_uuid\n" +
                "where teacher_child_nationalities_pvt.teacher_child_uuid = '" + teacherChildUUID +
                "' and nationalities.deleted_at is null\n" +
                "and teacher_child_nationalities_pvt.deleted_at is null\n" +
                "and (nationalities.name ILIKE '%" + name + "%'" +
                "or nationalities.description ILIKE '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveNationalityEntity> showTeacherChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join teacher_child_nationalities_pvt \n" +
                "on nationalities.uuid = teacher_child_nationalities_pvt.nationality_uuid\n" +
                "where teacher_child_nationalities_pvt.teacher_child_uuid = '" + teacherChildUUID +
                "' and nationalities.deleted_at is null\n" +
                "and teacher_child_nationalities_pvt.deleted_at is null\n" +
                "and nationalities.status = "+ status +
                " and (nationalities.name ILIKE '%" + name + "%'" +
                "or nationalities.description ILIKE '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }
}
