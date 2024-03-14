package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.NotificationTypeEntity;

import java.util.UUID;

@Repository
public interface NotificationTypeRepository extends ReactiveCrudRepository<NotificationTypeEntity, Long> {
    Mono<NotificationTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<NotificationTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<NotificationTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<NotificationTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name,UUID uuid);
}
