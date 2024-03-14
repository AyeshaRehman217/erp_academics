package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSiblingHobbyPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomHobbyMapper;

import java.util.UUID;

public class SlaveCustomTeacherSiblingHobbyPvtRepositoryImpl implements SlaveCustomTeacherSiblingHobbyPvtRepository {
    private DatabaseClient client;
    private SlaveHobbyEntity slaveHobbyEntity;

    @Autowired
    public SlaveCustomTeacherSiblingHobbyPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveHobbyEntity> existingTeacherSiblingHobbiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT hobbies.* FROM hobbies\n" +
                "WHERE hobbies.uuid NOT IN(\n" +
                "SELECT hobbies.uuid FROM hobbies\n" +
                "LEFT JOIN teacher_sibling_hobbies_pvt\n" +
                "ON teacher_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid\n" +
                "WHERE teacher_sibling_hobbies_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' AND teacher_sibling_hobbies_pvt.deleted_at IS NULL\n" +
                "AND hobbies.deleted_at IS NULL)\n" +
                "AND (hobbies.name ILIKE '%" + name + "%' " +
                "or hobbies.description ILIKE '%" + description + "%') " +
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
    public Flux<SlaveHobbyEntity> existingTeacherSiblingHobbiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT hobbies.* FROM hobbies\n" +
                "WHERE hobbies.uuid NOT IN(\n" +
                "SELECT hobbies.uuid FROM hobbies\n" +
                "LEFT JOIN teacher_sibling_hobbies_pvt\n" +
                "ON teacher_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid\n" +
                "WHERE teacher_sibling_hobbies_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' AND teacher_sibling_hobbies_pvt.deleted_at IS NULL\n" +
                "AND hobbies.deleted_at IS NULL)\n" +
                "AND (hobbies.name ILIKE '%" + name + "%' " +
                "or hobbies.description ILIKE '%" + description + "%') " +
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
    public Flux<SlaveHobbyEntity> showTeacherSiblingHobbiesList(UUID teacherSiblingUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select hobbies.* from hobbies\n" +
                "left join teacher_sibling_hobbies_pvt \n" +
                "on hobbies.uuid = teacher_sibling_hobbies_pvt.hobby_uuid\n" +
                "where teacher_sibling_hobbies_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' and hobbies.deleted_at is null\n" +
                "and teacher_sibling_hobbies_pvt.deleted_at is null\n" +
                "and (hobbies.name ILIKE '%" + name + "%' " +
                "or hobbies.description ILIKE '%" + description + "%') " +
                "order by hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveHobbyEntity> showTeacherSiblingHobbiesListWithStatus(UUID teacherSiblingUUID, Boolean status, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select hobbies.* from hobbies\n" +
                "left join teacher_sibling_hobbies_pvt \n" +
                "on hobbies.uuid = teacher_sibling_hobbies_pvt.hobby_uuid\n" +
                "where teacher_sibling_hobbies_pvt.teacher_sibling_uuid = '" + teacherSiblingUUID +
                "' and hobbies.deleted_at is null\n" +
                "and teacher_sibling_hobbies_pvt.deleted_at is null\n" +
                "and hobbies.status = "+ status +
                " and (hobbies.name ILIKE '%" + name + "%' " +
                "or hobbies.description ILIKE '%" + description + "%') " +
                "order by hobbies." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomHobbyMapper mapper = new SlaveCustomHobbyMapper();

        Flux<SlaveHobbyEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveHobbyEntity))
                .all();

        return result;
    }
}
