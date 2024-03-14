package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubLearningTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubLearningTypeRepository extends ReactiveCrudRepository<SubLearningTypeEntity, Long> {
    Mono<SubLearningTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubLearningTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubLearningTypeEntity> findByUuidAndBloomTaxonomyUUIDAndDeletedAtIsNull(UUID uuid,UUID bloomTaxonomyUUID);

    Mono<SubLearningTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SubLearningTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<SubLearningTypeEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    Mono<SubLearningTypeEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String code, UUID uuid);

    //fetch All Sub Learning Types
    Flux<SubLearningTypeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);
}
