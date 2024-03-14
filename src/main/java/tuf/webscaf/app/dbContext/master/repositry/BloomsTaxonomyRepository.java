package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.BloomTaxonomyEntity;

import java.util.UUID;

@Repository
public interface BloomsTaxonomyRepository extends ReactiveCrudRepository<BloomTaxonomyEntity, Long> {
    Mono<BloomTaxonomyEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<BloomTaxonomyEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<BloomTaxonomyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<BloomTaxonomyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
