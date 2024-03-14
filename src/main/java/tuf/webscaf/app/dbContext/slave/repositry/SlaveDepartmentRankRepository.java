package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentRankEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentRankRepository;

import java.util.UUID;

@Repository
public interface SlaveDepartmentRankRepository extends ReactiveSortingRepository<SlaveDepartmentRankEntity, Long>, SlaveCustomDepartmentRankRepository {
    Mono<SlaveDepartmentRankEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("SELECT count(*)\n" +
            "FROM department_ranks\n" +
            "LEFT JOIN department_rank_catalogues\n" +
            "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
            "LEFT JOIN departments\n" +
            "ON department_ranks.department_uuid = departments.uuid\n" +
            "WHERE departments.deleted_at IS NULL\n" +
            "AND department_ranks.deleted_at IS NULL\n" +
            "AND department_rank_catalogues.deleted_at IS NULL\n" +
            "AND concat(department_rank_catalogues.name, '|', departments.short_name) ILIKE concat('%',:name,'%')")
    Mono<Long> countAllRecordsWithName(String name);

    @Query("SELECT count(*)\n" +
            "FROM department_ranks\n" +
            "LEFT JOIN department_rank_catalogues\n" +
            "ON department_ranks.dept_rank_catalogue_uuid = department_rank_catalogues.uuid\n" +
            "LEFT JOIN departments\n" +
            "ON department_ranks.department_uuid = departments.uuid\n" +
            "WHERE departments.deleted_at IS NULL\n" +
            "AND department_ranks.deleted_at IS NULL\n" +
            "AND department_ranks.is_many= :many\n" +
            "AND department_rank_catalogues.deleted_at IS NULL\n" +
            "AND concat(department_rank_catalogues.name, '|', departments.short_name) ILIKE concat('%',:name,'%')")
    Mono<Long> countAllRecordsWithNameAndManyFilter(String name, Boolean many);

}
