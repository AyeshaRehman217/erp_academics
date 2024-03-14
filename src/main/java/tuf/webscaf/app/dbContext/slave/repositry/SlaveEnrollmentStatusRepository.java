package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveEnrollmentStatusEntity;

import java.util.UUID;

@Repository
public interface SlaveEnrollmentStatusRepository extends ReactiveCrudRepository<SlaveEnrollmentStatusEntity, Long> {
    Flux<SlaveEnrollmentStatusEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveEnrollmentStatusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveEnrollmentStatusEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveEnrollmentStatusEntity> findAllByReasonContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String reason);

    Flux<SlaveEnrollmentStatusEntity> findAllByReasonContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String reason, Boolean status);

    Mono<Long> countByReasonContainingIgnoreCaseAndDeletedAtIsNull(String reason);

    Mono<Long> countByReasonContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String reason, Boolean status);

}
