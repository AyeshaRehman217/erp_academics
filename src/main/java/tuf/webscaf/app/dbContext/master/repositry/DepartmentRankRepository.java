package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DepartmentRankEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRankRepository extends ReactiveSortingRepository<DepartmentRankEntity, Long> {
    Flux<DepartmentRankEntity> findAllByDepartmentUUIDAndDeptRankCatalogueUUIDInAndDeletedAtIsNull(UUID departmentUUID, List<UUID> deptRankCatalogueList);

    Mono<DepartmentRankEntity> findFirstByDepartmentUUIDAndDeptRankCatalogueUUIDAndDeletedAtIsNull(UUID departmentUUID, UUID deptRankCatalogueUUID);

    Mono<DepartmentRankEntity> findFirstByDepartmentUUIDAndDeptRankCatalogueUUIDAndDeletedAtIsNullAndUuidIsNot(UUID departmentUUID, UUID deptRankCatalogueUUID, UUID uuid);

    Mono<DepartmentRankEntity> findFirstByDeptRankCatalogueUUIDAndDeletedAtIsNull(UUID deptRankCatalogueUUID);

    Mono<DepartmentRankEntity> findFirstByDepartmentUUIDAndDeletedAtIsNull(UUID departmentUUID);

    Mono<DepartmentRankEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}

