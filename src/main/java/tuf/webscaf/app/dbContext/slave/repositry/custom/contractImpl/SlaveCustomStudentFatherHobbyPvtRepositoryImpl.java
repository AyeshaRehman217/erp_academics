package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentFatherHobbyPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomHobbyMapper;

import java.util.UUID;

public class SlaveCustomStudentFatherHobbyPvtRepositoryImpl implements SlaveCustomStudentFatherHobbyPvtRepository {
    private DatabaseClient client;
    private SlaveHobbyEntity slaveHobbyEntity;

    @Autowired
    public SlaveCustomStudentFatherHobbyPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveHobbyEntity> existingStudentFatherHobbiesList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT hobbies.* FROM hobbies\n" +
                "WHERE hobbies.uuid NOT IN(\n" +
                "SELECT hobbies.uuid FROM hobbies\n" +
                "LEFT JOIN std_fth_hobbies_pvt\n" +
                "ON std_fth_hobbies_pvt.hobby_uuid = hobbies.uuid\n" +
                "WHERE std_fth_hobbies_pvt.std_father_uuid = '" + studentFatherUUID +
                "' AND std_fth_hobbies_pvt.deleted_at IS NULL\n" +
                "AND hobbies.deleted_at IS NULL)\n" +
                "AND (hobbies.name ILIKE '%" + name + "%' or hobbies.description ILIKE '%" + description + "%')" +
                "AND hobbies.deleted_at IS NULL " +
                "ORDER BY hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHobbyEntity> existingStudentFatherHobbiesListWithStatusCheck(UUID studentFatherUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "SELECT hobbies.* FROM hobbies\n" +
                "WHERE hobbies.uuid NOT IN(\n" +
                "SELECT hobbies.uuid FROM hobbies\n" +
                "LEFT JOIN std_fth_hobbies_pvt\n" +
                "ON std_fth_hobbies_pvt.hobby_uuid = hobbies.uuid\n" +
                "WHERE std_fth_hobbies_pvt.std_father_uuid = '" + studentFatherUUID +
                "' AND std_fth_hobbies_pvt.deleted_at IS NULL\n" +
                "AND hobbies.deleted_at IS NULL)\n" +
                "AND (hobbies.name ILIKE '%" + name + "%' or" +
                " hobbies.description ILIKE  '%" + description + "%')" +
                "AND hobbies.deleted_at IS NULL " +
                "AND hobbies.status = " + status +
                " ORDER BY hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHobbyEntity> showStudentFatherHobbiesList(UUID studentFatherUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select hobbies.* from hobbies\n" +
                "left join std_fth_hobbies_pvt \n" +
                "on hobbies.uuid = std_fth_hobbies_pvt.hobby_uuid\n" +
                "where std_fth_hobbies_pvt.std_father_uuid = '" + studentFatherUUID +
                "' and hobbies.deleted_at is null\n" +
                "and std_fth_hobbies_pvt.deleted_at is null\n" +
                " and (hobbies.name ILIKE  '%" + name + "%' or " +
                "hobbies.description ILIKE  '%" + description + "%' )" +
                "order by hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHobbyEntity> showStudentFatherHobbiesListWithStatus(UUID studentFatherUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select hobbies.* from hobbies\n" +
                "left join std_fth_hobbies_pvt \n" +
                "on hobbies.uuid = std_fth_hobbies_pvt.hobby_uuid\n" +
                "where std_fth_hobbies_pvt.std_father_uuid = '" + studentFatherUUID +
                "' and hobbies.deleted_at is null\n" +
                "and std_fth_hobbies_pvt.deleted_at is null\n" +
                "and hobbies.status = " + status +
                " and (hobbies.name ILIKE  '%" + name + "%' or" +
                " hobbies.description ILIKE  '%" + description + "%' )" +
                "order by hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }

}
