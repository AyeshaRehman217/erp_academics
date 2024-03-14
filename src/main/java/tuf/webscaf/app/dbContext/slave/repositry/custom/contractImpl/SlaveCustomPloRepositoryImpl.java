package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomPloDtoMapper;

import java.util.UUID;

public class SlaveCustomPloRepositoryImpl implements SlaveCustomPloRepository {
    private DatabaseClient client;
    private SlavePloDto slavePloDto;

    @Autowired
    public SlaveCustomPloRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlavePloDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select plos.*, concat_ws('|',plos.code,plos.name,plos.description) as title \n" +
                " from plos\n" +
                " where plos.deleted_at is null \n" +
                " and plos.department_uuid= '" + departmentUUID +
                "' and plos.status= " + status +
                " and (concat_ws('|',plos.code,plos.name,plos.description) ILIKE '%" + title + "%' " +
                " OR plos.name ILIKE '%" + name + "%' OR plos.code ILIKE '%" + code + "%' OR plos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloDtoMapper mapper = new SlaveCustomPloDtoMapper();

        Flux<SlavePloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePloDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select plos.*, \n" +
                "CASE WHEN LENGTH(plos.name) > 0 THEN concat_ws('|',plos.name,plos.code,plos.description) \n" +
                "ELSE concat_ws('|',plos.code,plos.description) \n" +
                "END as title  \n" +
                "from plos \n" +
                "where plos.deleted_at is null \n" +
                "and plos.department_uuid= '" + departmentUUID +
                "' and (concat_ws('|',plos.code,plos.name,plos.description) ILIKE '%" + title + "%' " +
                " OR plos.name ILIKE '%" + name + "%' OR plos.code ILIKE '%" + code + "%' OR plos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloDtoMapper mapper = new SlaveCustomPloDtoMapper();

        Flux<SlavePloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloDto))
                .all();

        return result;
    }


    @Override
    public Flux<SlavePloDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select plos.*, \n" +
                "CASE WHEN LENGTH(plos.name) > 0 THEN concat_ws('|',plos.name,plos.code,plos.description) \n" +
                "ELSE concat_ws('|',plos.code,plos.description) \n" +
                "END as title  \n" +
                "from plos \n" +
                "where plos.deleted_at is null \n" +
                " and plos.status= " + status +
                " and (concat_ws('|',plos.code,plos.name,plos.description) ILIKE '%" + title + "%' " +
                " OR plos.name ILIKE '%" + name + "%' OR plos.code ILIKE '%" + code + "%' OR plos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloDtoMapper mapper = new SlaveCustomPloDtoMapper();

        Flux<SlavePloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePloDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select plos.*, \n" +
                "CASE WHEN LENGTH(plos.name) > 0 THEN concat_ws('|',plos.name,plos.code,plos.description) \n" +
                "ELSE concat_ws('|',plos.code,plos.description) \n" +
                "END as title  \n" +
                "from plos \n" +
                " where plos.deleted_at is null \n" +
                " and (concat_ws('|',plos.code,plos.name,plos.description) ILIKE '%" + title + "%' " +
                " OR plos.name ILIKE '%" + name + "%' OR plos.code ILIKE '%" + code + "%' OR plos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloDtoMapper mapper = new SlaveCustomPloDtoMapper();

        Flux<SlavePloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlavePloDto> showPloRecords(UUID ploUUID) {
        String query = " select plos.*, \n" +
                " CASE WHEN LENGTH(plos.name) > 0 THEN concat_ws('|',plos.name,plos.code,plos.description) \n" +
                " ELSE concat_ws('|',plos.code,plos.description) \n" +
                " END as title  \n" +
                " from plos \n" +
                " where plos.deleted_at is null and plos.uuid = '" + ploUUID +
                "'";

        SlaveCustomPloDtoMapper mapper = new SlaveCustomPloDtoMapper();

        Mono<SlavePloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloDto))
                .one();

        return result;
    }
}
