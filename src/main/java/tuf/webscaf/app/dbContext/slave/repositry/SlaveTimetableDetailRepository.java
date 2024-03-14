package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTimetableDetailEntity;

import java.util.UUID;

@Repository
public interface SlaveTimetableDetailRepository extends ReactiveCrudRepository<SlaveTimetableDetailEntity, Long> {
    Flux<SlaveTimetableDetailEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTimetableDetailEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveTimetableDetailEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTimetableDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
