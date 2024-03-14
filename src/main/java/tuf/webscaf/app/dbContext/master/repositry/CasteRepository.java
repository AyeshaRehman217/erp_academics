package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CasteEntity;

import java.util.UUID;

@Repository
public interface CasteRepository extends ReactiveCrudRepository<CasteEntity, Long> {
    Mono<CasteEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CasteEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CasteEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<CasteEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name,UUID uuid);
}
