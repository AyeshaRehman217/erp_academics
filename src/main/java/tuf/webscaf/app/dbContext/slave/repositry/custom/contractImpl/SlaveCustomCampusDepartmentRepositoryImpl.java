package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusDepartmentDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCampusDepartmentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCampusDepartmentMapper;

import java.util.UUID;

public class SlaveCustomCampusDepartmentRepositoryImpl implements SlaveCustomCampusDepartmentRepository {
    private DatabaseClient client;
    private SlaveCampusDepartmentDto slaveCampusDepartmentDto;

    @Autowired
    public SlaveCustomCampusDepartmentRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCampusDepartmentDto> campusDepartmentIndex(String name, String dp, String d, Integer size, Long page) {
        String query = "select campus_departments.*, concat(campuses.name,'|',departments.name) as key \n" +
                "from campus_departments \n" +
                "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
                "join departments  on departments.uuid = campus_departments.department_uuid\n" +
                "and campuses.deleted_at is null " +
                "and departments.deleted_at is null " +
                "and campus_departments.deleted_at is null " +
                " AND concat(campuses.name,'|',departments.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusDepartmentMapper mapper = new SlaveCustomCampusDepartmentMapper();

        Flux<SlaveCampusDepartmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusDepartmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select campus_departments.*, concat(campuses.name,'|',departments.name)  as key\n" +
                "from campus_departments \n" +
                "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
                "join departments  on departments.uuid = campus_departments.department_uuid\n" +
                "and campus_departments.status = " + status +
                " and campuses.deleted_at is null " +
                "and departments.deleted_at is null " +
                "and campus_departments.deleted_at is null " +
                "AND concat(campuses.name,'|',departments.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusDepartmentMapper mapper = new SlaveCustomCampusDepartmentMapper();

        Flux<SlaveCampusDepartmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusDepartmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithCampusAndStatus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select campus_departments.*, concat(campuses.name,'|',departments.name)  as key\n" +
                "from campus_departments \n" +
                "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
                "join departments  on departments.uuid = campus_departments.department_uuid\n" +
                "and campus_departments.status = " + status +
                " and campus_departments.campus_uuid = '" + campusUUID +
                "' and campuses.deleted_at is null " +
                "and departments.deleted_at is null " +
                "and campus_departments.deleted_at is null " +
                "AND concat(campuses.name,'|',departments.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusDepartmentMapper mapper = new SlaveCustomCampusDepartmentMapper();

        Flux<SlaveCampusDepartmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusDepartmentDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithCampus(UUID campusUUID, String name, String dp, String d, Integer size, Long page) {
        String query = "select campus_departments.*, concat(campuses.name,'|',departments.name)  as key\n" +
                "from campus_departments \n" +
                "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
                "join departments  on departments.uuid = campus_departments.department_uuid\n" +
                " and campus_departments.campus_uuid = '" + campusUUID +
                "' and campuses.deleted_at is null " +
                "and departments.deleted_at is null " +
                "and campus_departments.deleted_at is null " +
                "AND concat(campuses.name,'|',departments.name) ILIKE '%" + name + "%' " +
                " ORDER BY campus_departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCampusDepartmentMapper mapper = new SlaveCustomCampusDepartmentMapper();

        Flux<SlaveCampusDepartmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusDepartmentDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveCampusDepartmentDto> campusDepartmentShow(UUID uuid) {
        String query = "select campus_departments.*, concat(campuses.name,'|',departments.name)  as key\n" +
                "from campus_departments \n" +
                "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
                "join departments  on departments.uuid = campus_departments.department_uuid\n" +
                " and campus_departments.uuid = '" + uuid +
                "' and campuses.deleted_at is null " +
                "and departments.deleted_at is null " +
                "and campus_departments.deleted_at is null ";

        SlaveCustomCampusDepartmentMapper mapper = new SlaveCustomCampusDepartmentMapper();

        Mono<SlaveCampusDepartmentDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCampusDepartmentDto))
                .one();

        return result;
    }

}
