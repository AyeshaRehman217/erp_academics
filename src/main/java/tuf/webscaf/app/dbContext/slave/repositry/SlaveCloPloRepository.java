package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloPloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloPloRepository;

import java.util.UUID;

@Repository
public interface SlaveCloPloRepository extends ReactiveCrudRepository<SlaveCloPloEntity, Long>, SlaveCustomCloPloRepository {
    Mono<SlaveCloPloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCloPloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCloPloEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Flux<SlaveCloPloEntity> findAllByDeletedAtIsNullAndStatus(Boolean status, Pageable pageable);

    Mono<Long> countByDeletedAtIsNullAndStatus(Boolean status);

    @Query("select count(*) from clo_plos \n" +
            " join clos on clos.uuid=clo_plos.clo_uuid \n" +
            "join plos on plos.uuid=clo_plos.plo_uuid \n" +
            "join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid\n" +
            "join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid\n" +
            "where plos.department_uuid = clos.department_uuid\n" +
            "and clos.department_uuid = :departmentUUID\n" +
            "and  plos.department_uuid = :departmentUUID\n" +
            "and plos.deleted_at IS NULL \n" +
            "and clos.deleted_at IS NULL \n" +
            "and clo_plos.deleted_at IS NULL\n" +
            "and bloom_taxonomies.deleted_at IS NULL\n" +
            "and sub_learning_types.deleted_at IS NULL\n" +
            "and concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countCloPloRecordsWithDepartmentUUID(UUID departmentUUID, String key);

    @Query("select count(*) from clo_plos \n" +
            " join clos on clos.uuid=clo_plos.clo_uuid \n" +
            "join plos on plos.uuid=clo_plos.plo_uuid \n" +
            "join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid\n" +
            "join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid\n" +
            "where plos.department_uuid = clos.department_uuid\n" +
            "and clos.department_uuid = :departmentUUID\n" +
            "and  plos.department_uuid = :departmentUUID\n" +
            "and  clo_plos.status = :status\n" +
            "and plos.deleted_at IS NULL \n" +
            "and clos.deleted_at IS NULL \n" +
            "and clo_plos.deleted_at IS NULL\n" +
            "and bloom_taxonomies.deleted_at IS NULL\n" +
            "and sub_learning_types.deleted_at IS NULL\n" +
            "and concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countCloPloRecordsWithDepartmentAndStatus(UUID departmentUUID, String key, Boolean status);

    @Query("select count(*) from clo_plos \n" +
            " join clos on clos.uuid=clo_plos.clo_uuid \n" +
            "join plos on plos.uuid=clo_plos.plo_uuid \n" +
            "join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid\n" +
            "join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid\n" +
            "where clo_plos.status = :status\n" +
            "and plos.deleted_at IS NULL \n" +
            "and clos.deleted_at IS NULL \n" +
            "and clo_plos.deleted_at IS NULL\n" +
            "and bloom_taxonomies.deleted_at IS NULL\n" +
            "and sub_learning_types.deleted_at IS NULL\n" +
            "and concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countCloPloRecordsAndStatus(String key, Boolean status);

    @Query("select count(*) from clo_plos \n" +
            " join clos on clos.uuid=clo_plos.clo_uuid \n" +
            "join plos on plos.uuid=clo_plos.plo_uuid \n" +
            "join bloom_taxonomies on bloom_taxonomies.uuid=clo_plos.bloom_taxonomy_uuid\n" +
            "join sub_learning_types on sub_learning_types.uuid=clo_plos.sub_learning_type_uuid\n" +
            "where plos.deleted_at IS NULL \n" +
            "and clos.deleted_at IS NULL \n" +
            "and clo_plos.deleted_at IS NULL\n" +
            "and bloom_taxonomies.deleted_at IS NULL\n" +
            "and sub_learning_types.deleted_at IS NULL\n" +
            "and concat(clos.code,'|',plos.code,'|',bloom_taxonomies.name,'|',sub_learning_types.code) \n" +
            " ILIKE concat('%',:key,'%') ")
    Mono<Long> countCloPloRecords(String key);

}
