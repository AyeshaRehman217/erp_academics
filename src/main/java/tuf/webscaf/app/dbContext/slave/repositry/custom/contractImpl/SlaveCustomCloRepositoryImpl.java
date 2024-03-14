package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloPloDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloPloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCloDtoMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCloPloMapper;

import java.util.UUID;

public class SlaveCustomCloRepositoryImpl implements SlaveCustomCloRepository {
    private DatabaseClient client;
    private SlaveCloDto slaveCloDto;

    @Autowired
    public SlaveCustomCloRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstDepartmentAndEmphasisLevelWithStatus(Boolean status, UUID departmentUUID, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                "and clos.department_uuid= '" + departmentUUID +
                "' and clos.emphasis_uuid= '" + emphasisLevelUUID +
                "' and clos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstDepartmentAndEmphasisLevelWithoutStatus(UUID departmentUUID, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                "and clos.department_uuid= '" + departmentUUID +
                "' and clos.emphasis_uuid= '" + emphasisLevelUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                "and clos.department_uuid= '" + departmentUUID +
                "' and clos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                "and clos.department_uuid= '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstEmphasisLevelWithStatus(Boolean status, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                " and clos.emphasis_uuid= '" + emphasisLevelUUID +
                "' and clos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsAgainstEmphasisLevelWithoutStatus(UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                " where clos.deleted_at is null \n" +
                " and clos.emphasis_uuid= '" + emphasisLevelUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                "where clos.deleted_at is null \n" +
                " and clos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select clos.*, \n" +
                "CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                "ELSE concat_ws('|',clos.code,clos.description) \n" +
                "END as title  \n" +
                "from clos \n" +
                " where clos.deleted_at is null \n" +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveCloDto> showCloRecords(UUID cloUUID) {
        String query = " select clos.*, \n" +
                " CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END as title  \n" +
                " from clos \n" +
                " where clos.deleted_at is null and clos.uuid = '" + cloUUID +
                "'";

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Mono<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .one();

        return result;
    }
}
