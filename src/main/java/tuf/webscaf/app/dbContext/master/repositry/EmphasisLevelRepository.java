package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.EmphasisLevelEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmphasisLevelRepository extends ReactiveCrudRepository<EmphasisLevelEntity, Long> {
    Mono<EmphasisLevelEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<EmphasisLevelEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<EmphasisLevelEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<EmphasisLevelEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
