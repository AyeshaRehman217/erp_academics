package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomDepartmentMapper;

import java.util.UUID;

public class SlaveCustomDepartmentRepositoryImpl implements SlaveCustomDepartmentRepository {
    private DatabaseClient client;
    private SlaveDepartmentEntity slaveDepartmentEntity;

    @Autowired
    public SlaveCustomDepartmentRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveDepartmentEntity> showMappedDepartmentList(UUID departmentRankUUID, String name, String description, String dp, String d, Integer size, Long page) {
        String query = "select departments.* " +
                "FROM departments\n" +
                "left join department_ranks \n" +
                "on departments.uuid = department_ranks.department_uuid\n" +
                "where department_ranks.uuid = '" + departmentRankUUID +
                "' and departments.deleted_at is null\n" +
                "and department_ranks.deleted_at is null\n" +
                "AND (departments.name ILIKE '%" + name + "%' or " +
                "departments.description ILIKE '%" + description + "%')" +
                "order by departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentMapper mapper = new SlaveCustomDepartmentMapper();

        Flux<SlaveDepartmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentEntity))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveDepartmentEntity> showMappedDepartmentListWithStatus(UUID departmentRankUUID, String name, String description, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select departments.* " +
        "FROM departments\n" +
                "left join department_ranks \n" +
                "on departments.uuid = department_ranks.department_uuid\n" +
                "where department_ranks.uuid = '" + departmentRankUUID +
                "' and departments.deleted_at is null\n" +
                "and department_ranks.deleted_at is null\n" +
                "AND (departments.name ILIKE '%" + name + "%' or " +
                "departments.description ILIKE '%" + description + "%')" +
                "and departments.status = "+ status +
                " order by departments." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentMapper mapper = new SlaveCustomDepartmentMapper();

        Flux<SlaveDepartmentEntity> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentEntity))
                .all();

        return result;
    }
}
