package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubLearningTypeEntity;

import java.util.UUID;

@Repository
public interface SlaveSubLearningTypeRepository extends ReactiveCrudRepository<SlaveSubLearningTypeEntity, Long> {
    Mono<SlaveSubLearningTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubLearningTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Fetch All Records Without Status Filter and With Bloom Taxonomy UUID
     **/
    Flux<SlaveSubLearningTypeEntity> findAllByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID bloomTaxonomyUUID, String code, UUID bloomTaxonomyUUID1, String description, UUID bloomTaxonomyUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNull(String name, UUID bloomTaxonomyUUID, String code, UUID bloomTaxonomyUUID1, String description, UUID bloomTaxonomyUUID2);

    /**
     * Fetch All Records Without status and Bloom Taxonomy UUID
     **/
    Flux<SlaveSubLearningTypeEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String code, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String code, String description);

    /**
     * Fetch All Records With Status
     **/
    Flux<SlaveSubLearningTypeEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String code, Boolean status1, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String code, Boolean status1, String description, Boolean status2);


    /**
     * Fetch All Records With Status & Bloom Taxonomy Filter
     **/
    Flux<SlaveSubLearningTypeEntity> findAllByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID bloomTaxonomyUUID, Boolean status, String code, UUID bloomTaxonomyUUID1, Boolean status1, String description, UUID bloomTaxonomyUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNull(String name, UUID bloomTaxonomyUUID, Boolean status, String code, UUID bloomTaxonomyUUID1, Boolean status1, String description, UUID bloomTaxonomyUUID2, Boolean status2);

//    /**
//     * Count Un Mapped Sub learning Types against Bloom Taxonomy UUID with and Without Status Filter
//     **/
//    @Query("SELECT count(*) FROM sub_learning_types\n" +
//            "WHERE sub_learning_types.uuid NOT IN(\n" +
//            "SELECT sub_learning_types.uuid FROM sub_learning_types\n" +
//            "LEFT JOIN blooms_taxonomy_sub_learning_type_pvt\n" +
//            "ON blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid = sub_learning_types.uuid \n" +
//            "WHERE blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = :bloomTaxonomyUUID\n" +
//            "AND blooms_taxonomy_sub_learning_type_pvt.deleted_at IS NULL\n" +
//            "AND sub_learning_types.deleted_at IS NULL )\n" +
//            "AND sub_learning_types.deleted_at IS NULL " +
//            "and (sub_learning_types.name ilike concat('%',:name,'%') " +
//            "or sub_learning_types.code ilike concat('%',:code,'%')" +
//            "or sub_learning_types.description ilike concat('%',:description,'%')" +
//            ")")
//    Mono<Long> countUnMappedSubLearningTypesRecords(UUID bloomTaxonomyUUID, String name, String code, String description);
//
//
//    @Query("SELECT count(*) FROM sub_learning_types\n" +
//            "WHERE sub_learning_types.uuid NOT IN(\n" +
//            "SELECT sub_learning_types.uuid FROM sub_learning_types\n" +
//            "LEFT JOIN blooms_taxonomy_sub_learning_type_pvt\n" +
//            "ON blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid = sub_learning_types.uuid \n" +
//            "WHERE blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = :bloomTaxonomyUUID\n" +
//            "AND blooms_taxonomy_sub_learning_type_pvt.deleted_at IS NULL\n" +
//            "AND sub_learning_types.deleted_at IS NULL )\n" +
//            "AND sub_learning_types.deleted_at IS NULL " +
//            "AND sub_learning_types.status = :status " +
//            "and (sub_learning_types.name ilike concat('%',:name,'%') " +
//            "or sub_learning_types.code ilike concat('%',:code,'%')" +
//            "or sub_learning_types.description ilike concat('%',:description,'%')" +
//            ")")
//    Mono<Long> countUnMappedSubLearningTypesRecordsWithStatus(UUID bloomTaxonomyUUID, String name, String code, String description, Boolean status);
//
//    /**
//     * Count Mapped Sub learning Types against Bloom Taxonomy UUID with and Without Status Filter
//     **/
//    @Query("select count(*) from sub_learning_types\n" +
//            "left join blooms_taxonomy_sub_learning_type_pvt\n" +
//            "on sub_learning_types.uuid = blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid\n" +
//            "where blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = :bloomTaxonomyUUID\n" +
//            "and sub_learning_types.deleted_at is null\n" +
//            "and blooms_taxonomy_sub_learning_type_pvt.deleted_at is null\n" +
//            "and (sub_learning_types.name ilike concat('%',:name,'%') " +
//            "or sub_learning_types.code ilike concat('%',:code,'%')" +
//            "or sub_learning_types.description ilike concat('%',:description,'%')" +
//            ")")
//    Mono<Long> countMappedSubLearningTypesRecords(UUID bloomTaxonomyUUID, String name, String code, String description);
//
//
//    @Query("select count(*) from sub_learning_types\n" +
//            "left join blooms_taxonomy_sub_learning_type_pvt\n" +
//            "on sub_learning_types.uuid = blooms_taxonomy_sub_learning_type_pvt.sub_learning_type_uuid\n" +
//            "where blooms_taxonomy_sub_learning_type_pvt.bloom_taxonomy_uuid = :bloomTaxonomyUUID\n" +
//            "and sub_learning_types.deleted_at is null\n" +
//            "and sub_learning_types.status = :status " +
//            "and blooms_taxonomy_sub_learning_type_pvt.deleted_at is null\n" +
//            "and (sub_learning_types.name ilike concat('%',:name,'%') " +
//            "or sub_learning_types.code ilike concat('%',:code,'%')" +
//            "or sub_learning_types.description ilike concat('%',:description,'%')" +
//            ")")
//    Mono<Long> countMappedSubLearningTypesRecordsWithStatus(UUID bloomTaxonomyUUID, String name, String code, String description, Boolean status);
}
