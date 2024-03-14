package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DepartmentRankCatalogueEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRankCatalogueRepository extends ReactiveSortingRepository<DepartmentRankCatalogueEntity, Long> {
    Mono<DepartmentRankCatalogueEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DepartmentRankCatalogueEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<DepartmentRankCatalogueEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<DepartmentRankCatalogueEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<DepartmentRankCatalogueEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuidList);

}
