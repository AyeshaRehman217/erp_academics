package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CloPloEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CloPloRepository extends ReactiveCrudRepository<CloPloEntity, Long> {
    Mono<CloPloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CloPloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CloPloEntity> findFirstByPloUUIDAndDeletedAtIsNull(UUID ploUUID);

    Mono<CloPloEntity> findFirstByCloUUIDAndDeletedAtIsNull(UUID cloUUID);

    Flux<CloPloEntity> findAllByCloUUIDAndDeletedAtIsNull(UUID cloUUID);

    Mono<CloPloEntity> findFirstByCloUUIDAndPloUUIDAndBloomTaxonomyUUIDAndSubLearningTypeUUIDAndDeletedAtIsNull(UUID cloUUID, UUID ploUUID, UUID bloomTaxonomy, UUID subLearningTypeUUID);

    Mono<CloPloEntity> findFirstByCloUUIDAndPloUUIDAndBloomTaxonomyUUIDAndSubLearningTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID cloUUID, UUID ploUUID, UUID bloomTaxonomy, UUID subLearningTypeUUID, UUID uuid);

    Flux<CloPloEntity> findAllByCloUUIDAndPloUUIDInAndDeletedAtIsNull(UUID cloUUID, List<UUID> ids);
}
