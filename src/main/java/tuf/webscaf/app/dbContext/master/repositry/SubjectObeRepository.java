package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectObeEntity;

import java.util.UUID;

@Repository
public interface SubjectObeRepository extends ReactiveCrudRepository<SubjectObeEntity, Long> {
    Mono<SubjectObeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectObeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SubjectObeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
