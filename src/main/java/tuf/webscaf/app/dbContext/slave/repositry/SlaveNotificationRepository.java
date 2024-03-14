package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNotificationEntity;

import java.util.UUID;

@Repository
public interface SlaveNotificationRepository extends ReactiveCrudRepository<SlaveNotificationEntity, Long> {
    Mono<SlaveNotificationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveNotificationEntity> findAllBySubjectContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String subject);

    Flux<SlaveNotificationEntity> findAllBySubjectContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String subject, Boolean status);

    Mono<Long> countBySubjectContainingIgnoreCaseAndDeletedAtIsNull(String subject);

    Mono<Long> countBySubjectContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String subject, Boolean status);

    Mono<SlaveNotificationEntity> findByIdAndDeletedAtIsNull(Long id);
}
