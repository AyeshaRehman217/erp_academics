package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPeoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomPeoDtoMapper;

import java.util.UUID;

public class SlaveCustomPeoRepositoryImpl implements SlaveCustomPeoRepository {
    private DatabaseClient client;
    private SlavePeoDto slavePeoDto;

    @Autowired
    public SlaveCustomPeoRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlavePeoDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select peos.*, \n" +
                "CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                "ELSE concat_ws('|',peos.code,peos.description) \n" +
                "END as title  \n" +
                "from peos \n" +
                "where peos.deleted_at is null \n" +
                "and peos.department_uuid= '" + departmentUUID +
                "' and peos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select peos.*, \n" +
                "CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                "ELSE concat_ws('|',peos.code,peos.description) \n" +
                "END as title  \n" +
                "from peos \n" +
                "where peos.deleted_at is null \n" +
                "and peos.department_uuid= '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }


    @Override
    public Flux<SlavePeoDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select peos.*, \n" +
                "CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                "ELSE concat_ws('|',peos.code,peos.description) \n" +
                "END as title  \n" +
                "from peos \n" +
                "where peos.deleted_at is null \n" +
                " and peos.status= " + status +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = " select peos.*, \n" +
                "CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                "ELSE concat_ws('|',peos.code,peos.description) \n" +
                "END as title  \n" +
                "from peos \n" +
                " where peos.deleted_at is null \n" +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlavePeoDto> showPeoRecords(UUID peoUUID) {
        String query = " select peos.*, \n" +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title  \n" +
                " from peos \n" +
                " where peos.deleted_at is null and peos.uuid = '" + peoUUID +
                "'";

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Mono<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .one();

        return result;
    }
}
