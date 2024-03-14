package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.NotificationEntity;

import java.util.UUID;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {
    Mono<NotificationEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<NotificationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<NotificationEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<NotificationEntity> findFirstByNotificationTypeUUIDAndDeletedAtIsNull(UUID notificationTypeUUID);
}
