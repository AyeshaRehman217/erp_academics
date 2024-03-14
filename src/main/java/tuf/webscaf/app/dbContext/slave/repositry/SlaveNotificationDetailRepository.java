package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNotificationDetailEntity;

import java.util.UUID;

@Repository
public interface SlaveNotificationDetailRepository extends ReactiveCrudRepository<SlaveNotificationDetailEntity, Long> {
    Mono<SlaveNotificationDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveNotificationDetailEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String description);

    Flux<SlaveNotificationDetailEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String description, Boolean status);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String description);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String description, Boolean status);

    Mono<SlaveNotificationDetailEntity> findByIdAndDeletedAtIsNull(Long id);
}
