package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentVisionAndMissionDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentVisionAndMissionRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomDepartmentVisionAndMissionMapper;

public class SlaveCustomDepartmentVisionAndMissionRepositoryImpl implements SlaveCustomDepartmentVisionAndMissionRepository {
    private DatabaseClient client;
    private SlaveDepartmentVisionAndMissionDto slaveDepartmentVisionAndMissionDto;

    @Autowired
    public SlaveCustomDepartmentVisionAndMissionRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveDepartmentVisionAndMissionDto> departmentVisionAndMissionIndex(String vision, String mission, String dp, String d, Integer size, Long page) {
        String query = "select department_vision_and_missions.*, concat(departments.name,'|',department_vision_and_missions.vision) as key \n" +
                " from department_vision_and_missions \n" +
                " join departments  on departments.uuid = department_vision_and_missions.department_uuid \n" +
                " and departments.deleted_at is null \n" +
                " and department_vision_and_missions.deleted_at is null" +
                " and concat(departments.name,'|',department_vision_and_missions.vision) ILIKE '%" + vision + "%' " +
                " and concat(departments.name,'|',department_vision_and_missions.mission) ILIKE '%" + mission + "%' " +
                " ORDER BY department_vision_and_missions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentVisionAndMissionMapper mapper = new SlaveCustomDepartmentVisionAndMissionMapper();

        Flux<SlaveDepartmentVisionAndMissionDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentVisionAndMissionDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveDepartmentVisionAndMissionDto> departmentVisionAndMissionIndexWithStatus(String vision, String mission, Boolean status, String dp, String d, Integer size, Long page) {
        String query = "select department_vision_and_missions.*, concat(departments.name,'|',department_vision_and_missions.vision) as key \n" +
                " from department_vision_and_missions \n" +
                " join departments  on departments.uuid = department_vision_and_missions.department_uuid \n" +
                " and departments.deleted_at is null  +\n" +
                " and department_vision_and_missions.status "+ status +
                " and campuses.deleted_at is null \n" +
                " and courses.deleted_at is null \n" +
                " and campus_course.deleted_at is null \n" +
                " and department_vision_and_missions.deleted_at is null" +
                " and concat(departments.name,'|',department_vision_and_missions.vision) ILIKE '%" + vision + "%' " +
                " and concat(departments.name,'|',department_vision_and_missions.mission) ILIKE '%" + mission + "%' " +
                " ORDER BY department_vision_and_missions." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentVisionAndMissionMapper mapper = new SlaveCustomDepartmentVisionAndMissionMapper();

        Flux<SlaveDepartmentVisionAndMissionDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentVisionAndMissionDto))
                .all();

        return result;
    }

}
