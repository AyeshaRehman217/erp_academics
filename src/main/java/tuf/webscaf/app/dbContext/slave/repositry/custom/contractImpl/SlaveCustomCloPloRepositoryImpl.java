package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloPloDto;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloPloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomCloPloMapper;

import java.util.UUID;

public class SlaveCustomCloPloRepositoryImpl implements SlaveCustomCloPloRepository {
    private DatabaseClient client;
    private SlaveCloPloDto slaveCloPloDto;

    @Autowired
    public SlaveCustomCloPloRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveCloPloDto> indexWithDepartmentFilter(UUID departmentUUID, String key, String dp, String d, Integer size, Long page) {
        String query = " select *, concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) as key from clo_plos \n" +
                " join clos on clos.uuid=clo_plos.clo_uuid\n" +
                " join plos on plos.uuid=clo_plos.plo_uuid \n" +
                " join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid \n" +
                " join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid \n" +
                " where plos.department_uuid = clos.department_uuid\n" +
                " and clos.department_uuid = '" + departmentUUID +
                "' and  plos.department_uuid = '" + departmentUUID +
                "' and plos.deleted_at IS NULL \n" +
                " and clos.deleted_at IS NULL \n" +
                " and clo_plos.deleted_at IS NULL\n" +
                " and bloom_taxonomies.deleted_at IS NULL\n" +
                " and sub_learning_types.deleted_at IS NULL " +
                "AND concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) ILIKE '%" + key + "%' " +
                "ORDER BY clo_plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloPloMapper mapper = new SlaveCustomCloPloMapper();

        Flux<SlaveCloPloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloPloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloPloDto> indexWithDepartmentAndStatusFilter(UUID departmentUUID, Boolean status, String key, String dp, String d, Integer size, Long page) {
        String query = " select *, concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) as key from clo_plos \n" +
                " join clos on clos.uuid=clo_plos.clo_uuid\n" +
                " join plos on plos.uuid=clo_plos.plo_uuid \n" +
                " join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid \n" +
                " join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid \n" +
                " where plos.department_uuid = clos.department_uuid\n" +
                " and clos.department_uuid = '" + departmentUUID +
                "' and  plos.department_uuid = '" + departmentUUID +
                "' and clo_plos.status = " + status +
                " and plos.deleted_at IS NULL \n" +
                " and clos.deleted_at IS NULL \n" +
                " and clo_plos.deleted_at IS NULL\n" +
                " and bloom_taxonomies.deleted_at IS NULL\n" +
                " and sub_learning_types.deleted_at IS NULL " +
                "AND concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) ILIKE '%" + key + "%' " +
                "ORDER BY clo_plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloPloMapper mapper = new SlaveCustomCloPloMapper();

        Flux<SlaveCloPloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloPloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloPloDto> indexWithStatusFilter(Boolean status, String key, String dp, String d, Integer size, Long page) {
        String query = " select *, concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) as key from clo_plos \n" +
                " join clos on clos.uuid=clo_plos.clo_uuid\n" +
                " join plos on plos.uuid=clo_plos.plo_uuid \n" +
                " join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid \n" +
                " join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid \n" +
                " where clo_plos.status = " + status +
                " and plos.deleted_at IS NULL \n" +
                " and clos.deleted_at IS NULL \n" +
                " and clo_plos.deleted_at IS NULL\n" +
                " and bloom_taxonomies.deleted_at IS NULL\n" +
                " and sub_learning_types.deleted_at IS NULL " +
                "AND concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) ILIKE '%" + key + "%' " +
                "ORDER BY clo_plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloPloMapper mapper = new SlaveCustomCloPloMapper();

        Flux<SlaveCloPloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloPloDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveCloPloDto> index(String key, String dp, String d, Integer size, Long page) {
        String query = " select *, concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) as key from clo_plos \n" +
                " join clos on clos.uuid=clo_plos.clo_uuid\n" +
                " join plos on plos.uuid=clo_plos.plo_uuid \n" +
                " join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid \n" +
                " join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid \n" +
                " where plos.deleted_at IS NULL \n" +
                " and clos.deleted_at IS NULL \n" +
                " and clo_plos.deleted_at IS NULL\n" +
                " and bloom_taxonomies.deleted_at IS NULL\n" +
                " and sub_learning_types.deleted_at IS NULL " +
                "AND concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) ILIKE '%" + key + "%' " +
                "ORDER BY clo_plos." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomCloPloMapper mapper = new SlaveCustomCloPloMapper();

        Flux<SlaveCloPloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloPloDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveCloPloDto> showByUUID(UUID uuid) {
        String query = " select *, concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) as key from clo_plos \n" +
                " join clos on clos.uuid=clo_plos.clo_uuid\n" +
                " join plos on plos.uuid=clo_plos.plo_uuid \n" +
                " join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid \n" +
                " join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid \n" +
                " where plos.department_uuid = clos.department_uuid\n" +
                " and clo_plos.uuid ='" + uuid +
                "' and plos.deleted_at IS NULL \n" +
                " and clos.deleted_at IS NULL \n" +
                " and clo_plos.deleted_at IS NULL\n" +
                " and bloom_taxonomies.deleted_at IS NULL\n" +
                " and sub_learning_types.deleted_at IS NULL ";

        SlaveCustomCloPloMapper mapper = new SlaveCustomCloPloMapper();

        Mono<SlaveCloPloDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveCloPloDto))
                .one();

        return result;
    }
}
