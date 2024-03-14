package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloPeoPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPloPeoPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomPeoDtoMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomPeoMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomPloPeoPvtMapper;

import java.util.UUID;

public class SlaveCustomPloPeoPvtRepositoryImpl implements SlaveCustomPloPeoPvtRepository {
    private DatabaseClient client;
    private SlavePeoEntity slavePeoEntity;
    private SlavePeoDto slavePeoDto;
    private SlavePloPeoPvtDto slavePloPeoPvtDto;

    @Autowired
    public SlaveCustomPloPeoPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlavePeoDto> showUnMappedPloPeoList(UUID ploUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title " +
                "FROM peos\n" +
                "WHERE peos.uuid NOT IN(\n" +
                "SELECT peos.uuid FROM peos\n" +
                "LEFT JOIN plo_peos_pvt\n" +
                "ON plo_peos_pvt.peo_uuid = peos.uuid\n" +
                "WHERE plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' AND plo_peos_pvt.deleted_at IS NULL\n" +
                "AND peos.deleted_at IS NULL)\n" +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "AND peos.deleted_at IS NULL " +
                "ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showUnMappedPloPeoListWithStatus(UUID ploUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title " +
                " FROM peos\n" +
                "WHERE peos.uuid NOT IN(\n" +
                "SELECT peos.uuid FROM peos\n" +
                "LEFT JOIN plo_peos_pvt\n" +
                "ON plo_peos_pvt.peo_uuid = peos.uuid\n" +
                "WHERE plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' AND plo_peos_pvt.deleted_at IS NULL\n" +
                "AND peos.deleted_at IS NULL)\n" +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "AND peos.deleted_at IS NULL " +
                "AND peos.status = " + status +
                " ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showUnMappedPloPeoListAgainstDepartment(UUID departmentUUID, UUID ploUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title " +
                " FROM peos \n" +
                "WHERE peos.uuid NOT IN(\n" +
                "SELECT peos.uuid FROM peos\n" +
                "LEFT JOIN plo_peos_pvt\n" +
                "ON plo_peos_pvt.peo_uuid = peos.uuid\n" +
                "WHERE plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' AND plo_peos_pvt.deleted_at IS NULL\n" +
                "AND peos.deleted_at IS NULL)\n" +
                "AND peos.department_uuid = '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "AND peos.deleted_at IS NULL " +
                "AND peos.department_uuid = '" + departmentUUID +
                "' ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showUnMappedPloPeoListWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT peos.* ," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title \n " +
                " FROM peos \n" +
                " WHERE peos.uuid NOT IN(\n" +
                "SELECT peos.uuid FROM peos\n" +
                "LEFT JOIN plo_peos_pvt\n" +
                "ON plo_peos_pvt.peo_uuid = peos.uuid\n" +
                "WHERE plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' AND plo_peos_pvt.deleted_at IS NULL\n" +
                "AND peos.deleted_at IS NULL)\n" +
                "AND peos.department_uuid = '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "AND peos.deleted_at IS NULL " +
                "AND peos.status = " + status +
                " AND peos.department_uuid = '" + departmentUUID +
                "' ORDER BY peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showMappedPloPeoList(UUID ploUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title \n " +
                " from peos\n" +
                " left join plo_peos_pvt on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                " where plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' and peos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "order by peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showMappedPloPeoListWithStatus(UUID ploUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title \n " +
                " from peos\n" +
                "left join plo_peos_pvt \n" +
                "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                "where plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' and peos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                "and peos.status = " + status +
                " and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                "order by peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showMappedPloPeoListAgainstDepartment(UUID departmentUUID, UUID ploUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select peos.*," +
                " CASE WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END as title \n " +
                " from peos\n" +
                " left join plo_peos_pvt \n" +
                " on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                " where plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' and peos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                "AND peos.department_uuid = '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " order by peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePeoDto> showMappedPloPeoListWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select peos.* from peos\n" +
                "left join plo_peos_pvt \n" +
                "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                "where plo_peos_pvt.plo_uuid = '" + ploUUID +
                "' and peos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                "and peos.status = " + status +
                " AND peos.department_uuid = '" + departmentUUID +
                "' and (CASE \n" +
                " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
                " ELSE concat_ws('|',peos.code,peos.description) \n" +
                " END ILIKE '%" + title + "%'  OR peos.name ILIKE '%" + name + "%' OR peos.code ILIKE '%" + code + "%' OR peos.description ILIKE '%" + description + "%'  )" +
                " order by peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPeoDtoMapper mapper = new SlaveCustomPeoDtoMapper();

        Flux<SlavePeoDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePeoDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePloPeoPvtDto> index(String key, String dp, String d, Integer size, Long page) {
        String query = "select *,concat(plos.code,'|',peos.code) as key from plo_peos_pvt\n" +
                "join peos on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                "join plos on plos.uuid = plo_peos_pvt.plo_uuid\n" +
                "where peos.deleted_at is null\n" +
                "and plos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                " AND  concat(plos.code,'|',peos.code) ILIKE '%" + key + "%'" +
                "order by peos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloPeoPvtMapper mapper = new SlaveCustomPloPeoPvtMapper();

        Flux<SlavePloPeoPvtDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloPeoPvtDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlavePloPeoPvtDto> indexWithDepartment(UUID departmentUUID, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(plos.code,'|',peos.code) as key from plo_peos_pvt\n" +
                "join peos on peos.uuid = plo_peos_pvt.peo_uuid\n" +
                "join plos on plos.uuid = plo_peos_pvt.plo_uuid\n" +
                "where plos.department_uuid=peos.department_uuid " +
                "and  peos.department_uuid ='" + departmentUUID +
                "'and  plos.department_uuid ='" + departmentUUID +
                "'and  peos.deleted_at is null\n" +
                "and plo_peos_pvt.deleted_at is null\n" +
                "and plos.deleted_at is null\n" +
                " AND  concat(plos.code,'|',peos.code) ILIKE '%" + key + "%'" +
                "order by peos." + dp + " " + d +


                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomPloPeoPvtMapper mapper = new SlaveCustomPloPeoPvtMapper();

        Flux<SlavePloPeoPvtDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slavePloPeoPvtDto))
                .all();

        return result;
    }
}
