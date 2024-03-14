package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentChildNationalityPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomNationalityMapper;

import java.util.UUID;

public class SlaveCustomStudentChildNationalityPvtRepositoryImpl implements SlaveCustomStudentChildNationalityPvtRepository {
    private DatabaseClient client;
    private SlaveNationalityEntity slaveNationalityEntity;

    @Autowired
    public SlaveCustomStudentChildNationalityPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveNationalityEntity> existingStudentChildNationalitiesList(UUID teacherChildUUID, String name,String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN std_child_nationalities_pvt\n" +
                "ON std_child_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE std_child_nationalities_pvt.std_child_uuid = '" + teacherChildUUID +
                "' AND std_child_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                " AND (nationalities.name ILIKE '%" + name + "%'" +
                " or nationalities.description ILIKE '%" + description + "%')" +
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
    public Flux<SlaveNationalityEntity> existingStudentChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN std_child_nationalities_pvt\n" +
                "ON std_child_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE std_child_nationalities_pvt.std_child_uuid = '" + teacherChildUUID +
                "' AND std_child_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                " AND (nationalities.name ILIKE '%" + name + "%'" +
                " or nationalities.description ILIKE '%" + description + "%')" +
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
    public Flux<SlaveNationalityEntity> showStudentChildNationalitiesList(UUID teacherChildUUID, String name,String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join std_child_nationalities_pvt \n" +
                "on nationalities.uuid = std_child_nationalities_pvt.nationality_uuid\n" +
                "where std_child_nationalities_pvt.std_child_uuid = '" + teacherChildUUID +
                "' and nationalities.deleted_at is null\n" +
                "and std_child_nationalities_pvt.deleted_at is null\n" +
                " AND (nationalities.name ILIKE '%" + name + "%'" +
                " or nationalities.description ILIKE '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveNationalityEntity> showStudentChildNationalitiesListWithStatus(UUID teacherChildUUID, Boolean status, String name,String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join std_child_nationalities_pvt \n" +
                "on nationalities.uuid = std_child_nationalities_pvt.nationality_uuid\n" +
                "where std_child_nationalities_pvt.std_child_uuid = '" + teacherChildUUID +
                "' and nationalities.deleted_at is null\n" +
                "and std_child_nationalities_pvt.deleted_at is null\n" +
                "and nationalities.status = "+ status +
                " AND (nationalities.name ILIKE '%" + name + "%'" +
                " or nationalities.description ILIKE '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }
}
