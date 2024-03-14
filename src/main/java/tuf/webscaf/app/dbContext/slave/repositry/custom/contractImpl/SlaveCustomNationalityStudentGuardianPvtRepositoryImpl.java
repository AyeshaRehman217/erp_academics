package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomNationalityStudentGuardianPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomNationalityMapper;

import java.util.UUID;

public class SlaveCustomNationalityStudentGuardianPvtRepositoryImpl implements SlaveCustomNationalityStudentGuardianPvtRepository {
    private DatabaseClient client;
    private SlaveNationalityEntity slaveNationalityEntity;

    @Autowired
    public SlaveCustomNationalityStudentGuardianPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveNationalityEntity> existingNationalityList(UUID stdGuardianUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN std_grd_nationalities_pvt\n" +
                "ON std_grd_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE std_grd_nationalities_pvt.std_guardian_uuid = '" + stdGuardianUUID +
                "' AND std_grd_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                "AND (nationalities.name ILIKE '%" + name + "%' or " +
                "nationalities.description ILIKE  '%" + description + "%')" +
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
    public Flux<SlaveNationalityEntity> existingStudentGuardianNationalitiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT nationalities.* FROM nationalities\n" +
                "WHERE nationalities.uuid NOT IN(\n" +
                "SELECT nationalities.uuid FROM nationalities\n" +
                "LEFT JOIN std_grd_nationalities_pvt\n" +
                "ON std_grd_nationalities_pvt.nationality_uuid = nationalities.uuid\n" +
                "WHERE std_grd_nationalities_pvt.std_guardian_uuid = '" + studentGuardianUUID +
                "' AND std_grd_nationalities_pvt.deleted_at IS NULL\n" +
                "AND nationalities.deleted_at IS NULL)\n" +
                "AND (nationalities.name ILIKE '%" + name + "%' " +
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
    public Flux<SlaveNationalityEntity> showStudentGuardianNationalitiesList(UUID studentGuardianUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join std_grd_nationalities_pvt \n" +
                "on nationalities.uuid = std_grd_nationalities_pvt.nationality_uuid\n" +
                "where std_grd_nationalities_pvt.std_guardian_uuid = '" + studentGuardianUUID +
                "' and nationalities.deleted_at is null\n" +
                "and std_grd_nationalities_pvt.deleted_at is null\n" +
                "and (nationalities.name ilike '%" + name + "%' " +
                "or nationalities.description ilike '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveNationalityEntity> showStudentGuardianNationalitiesListWithStatus(UUID studentGuardianUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select nationalities.* from nationalities\n" +
                "left join std_grd_nationalities_pvt \n" +
                "on nationalities.uuid = std_grd_nationalities_pvt.nationality_uuid\n" +
                "where std_grd_nationalities_pvt.std_guardian_uuid = '" + studentGuardianUUID +
                "' and nationalities.deleted_at is null\n" +
                "and std_grd_nationalities_pvt.deleted_at is null\n" +
                "and nationalities.status = " + status +
                " and (nationalities.name ilike '%" + name + "%' " +
                "or nationalities.description ilike '%" + description + "%')" +
                "order by nationalities." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomNationalityMapper mapper = new SlaveCustomNationalityMapper();

        Flux<SlaveNationalityEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveNationalityEntity))
                .all();

        return result;
    }
}
