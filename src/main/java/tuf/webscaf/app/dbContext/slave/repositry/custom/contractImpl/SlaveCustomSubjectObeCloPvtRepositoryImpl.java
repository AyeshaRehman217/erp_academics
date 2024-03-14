package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectObeCloPvtDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectObeCloPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectObeCloPvtRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCloDtoMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCloMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSubjectObeCloPvtMapper;

import java.util.UUID;

public class SlaveCustomSubjectObeCloPvtRepositoryImpl implements SlaveCustomSubjectObeCloPvtRepository {
    private DatabaseClient client;
    private SlaveCloEntity slaveCloEntity;
    private SlaveSubjectObeCloPvtDto slaveSubjectObeCloPvtDto;
    private SlaveCloDto slaveCloDto;

    @Autowired
    public SlaveCustomSubjectObeCloPvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCloDto> unMappedClosList(UUID subjectObeUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT clos.*," +
                " CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END as title  \n" +
                " FROM clos\n" +
                "WHERE clos.uuid NOT IN(\n" +
                "SELECT clos.uuid FROM clos\n" +
                "LEFT JOIN subject_obe_clos_pvt \n" +
                "ON subject_obe_clos_pvt.clo_uuid = clos.uuid\n" +
                "WHERE subject_obe_clos_pvt.subject_obe_uuid = '" + subjectObeUUID +
                "' AND subject_obe_clos_pvt.deleted_at IS NULL\n" +
                "AND clos.deleted_at IS NULL)\n" +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' " +
                " OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                "AND clos.deleted_at IS NULL " +
                "ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> unMappedClosListWithStatus(UUID subjectObeUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "SELECT clos.*," +
                " CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END as title  \n" +
                " FROM clos\n" +
                "WHERE clos.uuid NOT IN(\n" +
                "SELECT clos.uuid FROM clos\n" +
                "LEFT JOIN subject_obe_clos_pvt \n" +
                "ON subject_obe_clos_pvt.clo_uuid = clos.uuid\n" +
                "WHERE subject_obe_clos_pvt.subject_obe_uuid = '" + subjectObeUUID +
                "' AND subject_obe_clos_pvt.deleted_at IS NULL\n" +
                "AND clos.deleted_at IS NULL)\n" +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' " +
                " OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                "AND clos.deleted_at IS NULL " +
                "AND clos.status = " + status +
                " ORDER BY clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> showClosList(UUID subjectObeUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select clos.*," +
                " CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END as title  \n" +
                " from clos \n" +
                " left join subject_obe_clos_pvt \n" +
                " on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
                " where subject_obe_clos_pvt.subject_obe_uuid = '" + subjectObeUUID +
                "' and clos.deleted_at is null\n" +
                " and subject_obe_clos_pvt.deleted_at is null\n" +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' " +
                " OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " order by clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloDto> showClosListWithStatus(UUID subjectObeUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page) {
        String query = "select clos.*," +
                " CASE WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END as title  \n" +
                " from clos\n" +
                " left join subject_obe_clos_pvt \n" +
                " on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
                " where subject_obe_clos_pvt.subject_obe_uuid = '" + subjectObeUUID +
                "' and clos.deleted_at is null\n" +
                " and subject_obe_clos_pvt.deleted_at is null\n" +
                " and (CASE \n" +
                " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
                " ELSE concat_ws('|',clos.code,clos.description) \n" +
                " END ILIKE '%" + title + "%'  OR clos.name ILIKE '%" + name + "%' " +
                " OR clos.code ILIKE '%" + code + "%' OR clos.description ILIKE '%" + description + "%'  )" +
                " and clos.status = " + status +
                " order by clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloDtoMapper mapper = new SlaveCustomCloDtoMapper();

        Flux<SlaveCloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectObeCloPvtDto> index(String key, String dp, String d, Integer size, Long page) {
        String query = "select *,concat(subject_obes.name,'|',clos.code) as key from subject_obe_clos_pvt\n" +
                "join clos on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
                "join subject_obes on subject_obes.uuid = subject_obe_clos_pvt.subject_obe_uuid\n" +
                "where clos.deleted_at is null\n" +
                "and subject_obes.deleted_at is null\n" +
                "and subject_obe_clos_pvt.deleted_at is null\n" +
                " AND  concat(subject_obes.name,'|',clos.code) ILIKE '%" + key + "%'" +
                "order by clos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectObeCloPvtMapper mapper = new SlaveCustomSubjectObeCloPvtMapper();

        Flux<SlaveSubjectObeCloPvtDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectObeCloPvtDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveSubjectObeCloPvtDto> indexWithDepartment(UUID departmentUUID, String key, String dp, String d, Integer size, Long page) {
        String query = "select *, concat(subject_obes.name,'|',clos.code) as key from subject_obe_clos_pvt\n" +
                "join clos on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
                "join subject_obes on subject_obes.uuid = subject_obe_clos_pvt.subject_obe_uuid\n" +
                "where  clos.department_uuid ='" + departmentUUID +
                "'and  clos.deleted_at is null\n" +
                "and subject_obe_clos_pvt.deleted_at is null\n" +
                "and subject_obes.deleted_at is null\n" +
                " AND  concat(subject_obes.name,'|',clos.code) ILIKE '%" + key + "%'" +
                "order by clos." + dp + " " + d +


                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomSubjectObeCloPvtMapper mapper = new SlaveCustomSubjectObeCloPvtMapper();

        Flux<SlaveSubjectObeCloPvtDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveSubjectObeCloPvtDto))
                .all();

        return result;
    }
}
