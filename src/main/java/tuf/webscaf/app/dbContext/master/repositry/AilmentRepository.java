package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AilmentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AilmentRepository extends ReactiveCrudRepository<AilmentEntity, Long> {
    Mono<AilmentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AilmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<AilmentEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Flux<AilmentEntity> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    Mono<AilmentEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AilmentEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
