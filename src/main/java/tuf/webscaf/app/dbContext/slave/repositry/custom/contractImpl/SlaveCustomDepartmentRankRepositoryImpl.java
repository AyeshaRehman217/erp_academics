package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentRankDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentRankCatalogueEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentRankRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomDepartmentRankCatalogueMapper;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomDepartmentRankMapper;

import java.util.UUID;

public class SlaveCustomDepartmentRankRepositoryImpl implements SlaveCustomDepartmentRankRepository {
    private DatabaseClient client;
    private SlaveDepartmentRankCatalogueEntity slaveDepartmentRankCatalogueEntity;
    private SlaveDepartmentRankDto slaveDepartmentRankDto;

    @Autowired
    public SlaveCustomDepartmentRankRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    @Override
    public Flux<SlaveDepartmentRankDto> showAllRecordsWithName(String name, String dp, String d, Integer size, Long page) {
        String query = "SELECT department_ranks.*, concat(department_rank_catalogues.name, '|', departments.short_name) as name\n" +
                "FROM department_ranks\n " +
                "LEFT JOIN department_rank_catalogues\n" +
                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
                "LEFT JOIN departments\n" +
                "ON department_ranks.department_uuid = departments.uuid\n" +
                "WHERE departments.deleted_at IS NULL\n" +
                "AND department_ranks.deleted_at IS NULL\n" +
                "AND department_rank_catalogues.deleted_at IS NULL\n" +
                "AND concat(department_rank_catalogues.name, '|', departments.short_name) ILIKE '%" + name + "%'" +
                " ORDER BY department_ranks." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentRankMapper mapper = new SlaveCustomDepartmentRankMapper();

        Flux<SlaveDepartmentRankDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentRankDto))
                .all();

        return result;
    }

    @Override
    public Flux<SlaveDepartmentRankDto> showAllRecordsWithNameAndManyFilter(String name, Boolean many, String dp, String d, Integer size, Long page) {
        String query = "SELECT department_ranks.*, concat(department_rank_catalogues.name, '|', departments.short_name) as name\n" +
                "FROM department_ranks\n " +
                "LEFT JOIN department_rank_catalogues\n" +
                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
                "LEFT JOIN departments\n" +
                "ON department_ranks.department_uuid = departments.uuid\n" +
                "WHERE departments.deleted_at IS NULL\n" +
                "AND department_ranks.deleted_at IS NULL\n" +
                "AND department_rank_catalogues.deleted_at IS NULL\n" +
                "AND concat(department_rank_catalogues.name, '|', departments.short_name) ILIKE '%" + name + "%' " +
                "AND department_ranks.is_many=" + many +
                " ORDER BY department_ranks." + dp + " " + d +
                " LIMIT " + size + " OFFSET " + page;

        SlaveCustomDepartmentRankMapper mapper = new SlaveCustomDepartmentRankMapper();

        Flux<SlaveDepartmentRankDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentRankDto))
                .all();

        return result;
    }

    @Override
    public Mono<SlaveDepartmentRankDto> showRecordWithName(UUID uuid) {
        String query = "SELECT department_ranks.*, concat(department_rank_catalogues.name, '|', departments.short_name) as name\n" +
                "FROM department_ranks\n " +
                "LEFT JOIN department_rank_catalogues\n" +
                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
                "LEFT JOIN departments\n" +
                "ON department_ranks.department_uuid = departments.uuid\n" +
                "WHERE department_ranks.uuid = '" + uuid + "'\n" +
                "AND departments.deleted_at IS NULL\n" +
                "AND department_ranks.deleted_at IS NULL\n" +
                "AND department_rank_catalogues.deleted_at IS NULL\n";

        SlaveCustomDepartmentRankMapper mapper = new SlaveCustomDepartmentRankMapper();

        Mono<SlaveDepartmentRankDto> result = client.sql(query)
                .map(row -> mapper.apply(row, slaveDepartmentRankDto))
                .first();

        return result;
    }

//    @Override
//    public Flux<SlaveDepartmentRankCatalogueEntity> showUnmappedDepartmentRankCatalogueList(UUID departmentUUID, String catalogueName, String catalogueDescription, String dp, String d, Integer size, Long page) {
//        String query = "SELECT department_rank_catalogues.* FROM department_rank_catalogues\n" +
//                "WHERE department_rank_catalogues.uuid NOT IN(\n" +
//                "SELECT department_rank_catalogues.uuid FROM department_rank_catalogues\n" +
//                "LEFT JOIN department_ranks\n" +
//                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
//                "WHERE department_ranks.department_uuid = '" + departmentUUID +
//                "' AND department_ranks.deleted_at IS NULL\n" +
//                "AND department_rank_catalogues.deleted_at IS NULL)\n" +
//                "AND (department_rank_catalogues.name ILIKE '%" + catalogueName + "%' OR " +
//                "department_rank_catalogues.description ILIKE '%" + catalogueDescription + "%')" +
//                "AND department_rank_catalogues.deleted_at IS NULL " +
//                " ORDER BY department_rank_catalogues." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomDepartmentRankCatalogueMapper mapper = new SlaveCustomDepartmentRankCatalogueMapper();
//
//        Flux<SlaveDepartmentRankCatalogueEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveDepartmentRankCatalogueEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveDepartmentRankCatalogueEntity> showUnmappedDepartmentRankCatalogueListWithStatus(UUID departmentUUID, String catalogueName, String catalogueDescription, Boolean status, String dp, String d, Integer size, Long page) {
//        String query = "SELECT department_rank_catalogues.* FROM department_rank_catalogues\n" +
//                "WHERE department_rank_catalogues.uuid NOT IN(\n" +
//                "SELECT department_rank_catalogues.uuid FROM department_rank_catalogues\n" +
//                "LEFT JOIN department_ranks\n" +
//                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
//                "WHERE department_ranks.department_uuid = '" + departmentUUID +
//                "' AND department_ranks.deleted_at IS NULL\n" +
//                "AND department_rank_catalogues.deleted_at IS NULL)\n" +
//                "AND (department_rank_catalogues.name ILIKE '%" + catalogueName + "%' OR " +
//                "department_rank_catalogues.description ILIKE '%" + catalogueDescription + "%')" +
//                "AND department_rank_catalogues.deleted_at IS NULL " +
//                "AND department_rank_catalogues.status = " + status +
//                " ORDER BY department_rank_catalogues." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomDepartmentRankCatalogueMapper mapper = new SlaveCustomDepartmentRankCatalogueMapper();
//
//        Flux<SlaveDepartmentRankCatalogueEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveDepartmentRankCatalogueEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveDepartmentRankCatalogueEntity> showMappedDepartmentRankCatalogueList(UUID departmentUUID, String catalogueName, String catalogueDescription, String dp, String d, Integer size, Long page) {
//        String query = "SELECT department_rank_catalogues.* FROM department_rank_catalogues\n" +
//                "LEFT JOIN department_ranks\n" +
//                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
//                "WHERE department_ranks.department_uuid = '" + departmentUUID +
//                "' AND department_ranks.deleted_at IS NULL\n" +
//                "AND department_rank_catalogues.deleted_at IS NULL\n" +
//                "AND (department_rank_catalogues.name ILIKE '%" + catalogueName + "%' OR " +
//                "department_rank_catalogues.description ILIKE '%" + catalogueDescription + "%')" +
//                " ORDER BY department_rank_catalogues." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomDepartmentRankCatalogueMapper mapper = new SlaveCustomDepartmentRankCatalogueMapper();
//
//        Flux<SlaveDepartmentRankCatalogueEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveDepartmentRankCatalogueEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveDepartmentRankCatalogueEntity> showMappedDepartmentRankCatalogueListWithStatus(UUID departmentUUID, String catalogueName, String catalogueDescription, Boolean status, String dp, String d, Integer size, Long page) {
//        String query = "SELECT department_rank_catalogues.* FROM department_rank_catalogues\n" +
//                "LEFT JOIN department_ranks\n" +
//                "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
//                "WHERE department_ranks.department_uuid = '" + departmentUUID +
//                "' AND department_ranks.deleted_at IS NULL\n" +
//                "AND department_rank_catalogues.deleted_at IS NULL\n" +
//                "AND (department_rank_catalogues.name ILIKE '%" + catalogueName + "%' OR " +
//                "department_rank_catalogues.description ILIKE '%" + catalogueDescription + "%')\n" +
//                "AND department_rank_catalogues.status = " + status +
//                " ORDER BY department_rank_catalogues." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomDepartmentRankCatalogueMapper mapper = new SlaveCustomDepartmentRankCatalogueMapper();
//
//        Flux<SlaveDepartmentRankCatalogueEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveDepartmentRankCatalogueEntity))
//                .all();
//
//        return result;
//    }


}
