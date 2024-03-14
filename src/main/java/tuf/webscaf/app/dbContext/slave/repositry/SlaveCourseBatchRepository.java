package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseBatchEntity;

import java.util.UUID;

@Repository
public interface SlaveCourseBatchRepository extends ReactiveCrudRepository<SlaveCourseBatchEntity, Long> {
    Mono<SlaveCourseBatchEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCourseBatchEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCourseBatchEntity> findAllByCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String code, String description);

    Mono<Long> countByCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String code, String description);
}
