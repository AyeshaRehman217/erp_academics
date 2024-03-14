package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomClassroomRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomClassroomMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherMapper;

import java.util.UUID;


public class SlaveCustomClassroomRepositoryImpl implements SlaveCustomClassroomRepository {
    private DatabaseClient client;
    private SlaveClassroomDto slaveClassroomDto;

    @Autowired
    public SlaveCustomClassroomRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveClassroomDto> indexWithoutStatus(String key, String code, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select classrooms.*, concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) as key\n" +
                " from classrooms \n" +
                " where \n" +
                " classrooms.deleted_at IS NULL\n" +
                " AND ( concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) ILIKE '%" + key + "%' \n" +
                "  or classrooms.code ILIKE '%" + code + "%' \n" +
                "    or classrooms.name ILIKE '%" + name + "%' \n" +
                "   or classrooms.description ILIKE '%" + name + "%' \n" +
                " ) " +
                " ORDER BY classrooms." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomClassroomMapper mapper = new SlaveCustomClassroomMapper();

        Flux<SlaveClassroomDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveClassroomDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveClassroomDto> indexWithStatus(Boolean status, String key, String code, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select classrooms.*, concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) as key\n" +
                " from classrooms \n" +
                " where \n" +
                " classrooms.deleted_at IS NULL " +
                " and classrooms.status = " + status +
                " AND ( concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) ILIKE '%" + key + "%' \n" +
                "  or classrooms.code ILIKE '%" + code + "%' \n" +
                "    or classrooms.name ILIKE '%" + name + "%' \n" +
                "   or classrooms.description ILIKE '%" + name + "%' \n" +
                " ) " +
                " ORDER BY classrooms." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomClassroomMapper mapper = new SlaveCustomClassroomMapper();

        Flux<SlaveClassroomDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveClassroomDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveClassroomDto> indexWithCampusWithoutStatusFilter(UUID campusUUID, String key, String code, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select classrooms.*, concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) as key\n" +
                "from classrooms \n" +
                "left join campuses \n" +
                " on classrooms.campus_uuid = campuses.uuid \n" +
                " where \n" +
                " classrooms.deleted_at IS NULL\n" +
                " and  campuses.deleted_at IS NULL\n" +
                " and classrooms.campus_uuid= '" + campusUUID +
                "' AND (concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) ILIKE '%" + key + "%' \n" +
                " or classrooms.code ILIKE '%" + code + "%' \n" +
                " or classrooms.name ILIKE '%" + name + "%' \n" +
                " or classrooms.description ILIKE '%" + description + "%' \n" +
                ")" +
                " ORDER BY classrooms." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomClassroomMapper mapper = new SlaveCustomClassroomMapper();

        Flux<SlaveClassroomDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveClassroomDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveClassroomDto> indexWithStatusAndCampus(UUID campusUUID, Boolean status, String key, String code, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select classrooms.*, concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) as key\n" +
                "from classrooms \n" +
                "left join campuses\n" +
                " on classrooms.campus_uuid = campuses.uuid\n" +
                " where \n" +
                " classrooms.deleted_at IS NULL\n" +
                " and  campuses.deleted_at IS NULL\n" +
                " and classrooms.status= " + status +
                " and classrooms.campus_uuid= '" + campusUUID +
                "' AND (concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) ILIKE '%" + key + "%' \n" +
                " or classrooms.code ILIKE '%" + code + "%' \n" +
                " or classrooms.name ILIKE '%" + name + "%' \n" +
                " or classrooms.description ILIKE '%" + description + "%' \n" +
                ")" +
                " ORDER BY classrooms." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomClassroomMapper mapper = new SlaveCustomClassroomMapper();

        Flux<SlaveClassroomDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveClassroomDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveClassroomDto> showByUuid(UUID uuid) {
        String query = "select classrooms.*, concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) as key\n" +
                " from classrooms\n" +
                " where classrooms.uuid= '" + uuid +
                "' and classrooms.deleted_at IS NULL\n";

        SlaveCustomClassroomMapper mapper = new SlaveCustomClassroomMapper();

        Mono<SlaveClassroomDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveClassroomDto))
                .one();

        return result;
    }

}
