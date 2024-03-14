//package tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl;
//
//import io.r2dbc.spi.ConnectionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.r2dbc.core.DatabaseClient;
//import reactor.core.publisher.Flux;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveSubLearningTypeEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomBloomsTaxonomySubLearningTypePvtRepository;
//import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomSubLearningTypeMapper;
//
//import java.util.UUID;
//
//public class SlaveCustomBloomsTaxonomySubLearningTypePvtRepositoryImpl implements SlaveCustomBloomsTaxonomySubLearningTypePvtRepository {
//    private DatabaseClient client;
//    private SlaveSubLearningTypeEntity slaveSubLearningTypeEntity;
//
//    @Autowired
//    public SlaveCustomBloomsTaxonomySubLearningTypePvtRepositoryImpl(@Qualifier("slave") ConnectionFactory cf) {
//        this.client = DatabaseClient.create(cf);
//    }
//
//    @Override
//    public Flux<SlaveSubLearningTypeEntity> unMappedSubLearningTypesAgainstBloomTaxonomy(UUID bloomTaxonomyUUID, String name, String code, String description, String dp, String d, Integer size, Long page) {
//        String query = "SELECT sub_learning_types.* FROM sub_learning_types\n" +
//                "WHERE sub_learning_types.uuid NOT IN(\n" +
//                "SELECT sub_learning_types.uuid FROM sub_learning_types\n" +
//                "LEFT JOIN blooms_taxonomy_sub_learning_type_pvt\n" +
//                "ON blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid = sub_learning_types.uuid\n" +
//                "WHERE blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = '" + bloomTaxonomyUUID +
//                "' AND blooms_taxonomy_sub_learning_type_pvt.deleted_at IS NULL\n" +
//                "AND sub_learning_types.deleted_at IS NULL)\n" +
//                "AND (sub_learning_types.name ILIKE '%" + name + "%'" +
//                "OR sub_learning_types.code ILIKE '%" + code + "%'" +
//                "OR sub_learning_types.description ILIKE  '%" + description + "%')" +
//                "AND sub_learning_types.deleted_at IS NULL " +
//                "ORDER BY sub_learning_types." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubLearningTypeMapper mapper = new SlaveCustomSubLearningTypeMapper();
//
//        Flux<SlaveSubLearningTypeEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubLearningTypeEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveSubLearningTypeEntity> unMappedSubLearningTypesAgainstBloomTaxonomyWithStatus(UUID bloomTaxonomyUUID, Boolean status, String name, String code, String description, String dp, String d, Integer size, Long page) {
//        String query = "SELECT sub_learning_types.* FROM sub_learning_types\n" +
//                "WHERE sub_learning_types.uuid NOT IN(\n" +
//                "SELECT sub_learning_types.uuid FROM sub_learning_types\n" +
//                "LEFT JOIN blooms_taxonomy_sub_learning_type_pvt\n" +
//                "ON blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid = sub_learning_types.uuid\n" +
//                "WHERE blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = '" + bloomTaxonomyUUID +
//                "' AND blooms_taxonomy_sub_learning_type_pvt.deleted_at IS NULL\n" +
//                "AND sub_learning_types.deleted_at IS NULL)\n" +
//                "AND (sub_learning_types.name ILIKE '%" + name + "%'" +
//                "OR sub_learning_types.code ILIKE '%" + code + "%'" +
//                "OR sub_learning_types.description ILIKE  '%" + description + "%')" +
//                "AND sub_learning_types.deleted_at IS NULL " +
//                "AND sub_learning_types.status = " + status +
//                " ORDER BY sub_learning_types." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubLearningTypeMapper mapper = new SlaveCustomSubLearningTypeMapper();
//
//        Flux<SlaveSubLearningTypeEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubLearningTypeEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveSubLearningTypeEntity> mappedSubLearningTypesAgainstBloomTaxonomy(UUID bloomTaxonomyUUID, String name, String code, String description, String dp, String d, Integer size, Long page) {
//        String query = "select sub_learning_types.* from sub_learning_types\n" +
//                "left join blooms_taxonomy_sub_learning_type_pvt \n" +
//                "on sub_learning_types.uuid = blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid\n" +
//                "where blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = '" + bloomTaxonomyUUID +
//                "' and sub_learning_types.deleted_at is null\n" +
//                "and blooms_taxonomy_sub_learning_type_pvt.deleted_at is null\n" +
//                "AND (sub_learning_types.name ILIKE '%" + name + "%'" +
//                "OR sub_learning_types.code ILIKE '%" + code + "%'" +
//                "OR sub_learning_types.description ILIKE  '%" + description + "%')" +
//                "order by sub_learning_types." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubLearningTypeMapper mapper = new SlaveCustomSubLearningTypeMapper();
//
//        Flux<SlaveSubLearningTypeEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubLearningTypeEntity))
//                .all();
//
//        return result;
//    }
//
//    @Override
//    public Flux<SlaveSubLearningTypeEntity> mappedSubLearningTypesAgainstBloomTaxonomyWithStatus(UUID bloomTaxonomyUUID, Boolean status, String name, String code, String description, String dp, String d, Integer size, Long page) {
//        String query = "select sub_learning_types.* from sub_learning_types\n" +
//                "left join blooms_taxonomy_sub_learning_type_pvt \n" +
//                "on sub_learning_types.uuid = blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid\n" +
//                "where blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = '" + bloomTaxonomyUUID +
//                "' and sub_learning_types.deleted_at is null\n" +
//                "and blooms_taxonomy_sub_learning_type_pvt.deleted_at is null\n" +
//                "AND (sub_learning_types.name ILIKE '%" + name + "%'" +
//                "OR sub_learning_types.code ILIKE '%" + code + "%'" +
//                "OR sub_learning_types.description ILIKE  '%" + description + "%')" +
//                "AND sub_learning_types.status = " + status +
//                " order by sub_learning_types." + dp + " " + d +
//                " LIMIT " + size + " OFFSET " + page;
//
//        SlaveCustomSubLearningTypeMapper mapper = new SlaveCustomSubLearningTypeMapper();
//
//        Flux<SlaveSubLearningTypeEntity> result = client.sql(query)
//                .map(row -> mapper.apply(row, slaveSubLearningTypeEntity))
//                .all();
//
//        return result;
//    }
//}
