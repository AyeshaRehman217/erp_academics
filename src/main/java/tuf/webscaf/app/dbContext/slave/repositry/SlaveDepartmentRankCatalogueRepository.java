package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentRankCatalogueEntity;

import java.util.UUID;

@Repository
public interface SlaveDepartmentRankCatalogueRepository extends ReactiveSortingRepository<SlaveDepartmentRankCatalogueEntity, Long> {
    Flux<SlaveDepartmentRankCatalogueEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveDepartmentRankCatalogueEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveDepartmentRankCatalogueEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveDepartmentRankCatalogueEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveDepartmentRankCatalogueEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    /**
     * Count Department Rank Catalogues Records that are not mapped against Department Rank Yet with Status and without Status Filter
     */
    //query used in pvt Department Rank Catalogues Department Rank PVT
    @Query("SELECT count(*) FROM department_rank_catalogues\n" +
            "WHERE department_rank_catalogues.uuid NOT IN(\n" +
            "SELECT department_rank_catalogues.uuid FROM department_rank_catalogues\n" +
            "LEFT JOIN department_ranks\n" +
            "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
            "WHERE department_ranks.department_uuid = :departmentUUID\n" +
            "AND department_rank_catalogues.deleted_at IS NULL\n" +
            "AND department_ranks.deleted_at IS NULL)\n" +
            "AND department_rank_catalogues.deleted_at IS NULL " +
            "AND (department_rank_catalogues.name ILIKE concat('%',:name,'%') " +
            "or department_rank_catalogues.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countUnmappedDepartmentRankCatalogueList(UUID departmentUUID, String name, String description);

    @Query("SELECT count(*) FROM department_rank_catalogues\n" +
            "WHERE department_rank_catalogues.uuid NOT IN(\n" +
            "SELECT department_rank_catalogues.uuid FROM department_rank_catalogues\n" +
            "LEFT JOIN department_ranks\n" +
            "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
            "WHERE department_ranks.department_uuid = :departmentUUID\n" +
            "AND department_rank_catalogues.deleted_at IS NULL\n" +
            "AND department_ranks.deleted_at IS NULL)\n" +
            "AND department_rank_catalogues.deleted_at IS NULL " +
            "AND department_rank_catalogues.status = :status " +
            "AND (department_rank_catalogues.name ILIKE concat('%',:name,'%') " +
            "or department_rank_catalogues.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countUnmappedDepartmentRankCatalogueRecordsWithStatus(UUID departmentUUID, String name, String description, Boolean status);

    /**
     * Count Department Rank Catalogues Records that are mapped against Department Rank with Status and without Status Filter
     */
    // query used for count of mapped department_rank_catalogues records for given student child profile
    @Query("SELECT count(*) FROM department_rank_catalogues\n" +
            "LEFT JOIN department_ranks \n" +
            "ON department_rank_catalogues.uuid = department_ranks.dept_rank_catalogue_uuid\n" +
            "WHERE department_ranks.department_uuid = :departmentUUID\n" +
            "AND department_ranks.deleted_at IS NULL\n" +
            "AND department_rank_catalogues.deleted_at IS NULL " +
            "AND (department_rank_catalogues.name ILIKE concat('%',:name,'%') " +
            "OR department_rank_catalogues.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedDepartmentRankCatalogue(UUID departmentRanUUID, String name, String description);

    // query used for count of mapped department rank catalogues records for given Department Rank
    @Query("SELECT count(*) FROM department_rank_catalogues\n" +
            "LEFT JOIN department_ranks \n" +
            "ON department_rank_catalogues.uuid = department_ranks.dept_rank_catalogue_uuid\n" +
            "WHERE department_ranks.department_uuid = :departmentUUID\n" +
            "AND department_ranks.deleted_at IS NULL\n" +
            "AND department_rank_catalogues.deleted_at IS NULL " +
            "AND department_rank_catalogues.status = :status " +
            "AND (department_rank_catalogues.name ILIKE concat('%',:name,'%') " +
            "OR department_rank_catalogues.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedDepartmentRankCatalogueWithStatus(UUID departmentRanUUID, String name, String description, Boolean status);

}
