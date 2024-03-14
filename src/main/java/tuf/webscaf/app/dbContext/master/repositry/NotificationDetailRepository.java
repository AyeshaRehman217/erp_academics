package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.NotificationDetailEntity;

import java.util.UUID;

@Repository
public interface NotificationDetailRepository extends ReactiveCrudRepository<NotificationDetailEntity, Long> {
    Mono<NotificationDetailEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<NotificationDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Campus Id Exists in Notification Details
    Mono<NotificationDetailEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<NotificationDetailEntity> findFirstByNotificationUUIDAndDeletedAtIsNull(UUID notificationUUID);
}
