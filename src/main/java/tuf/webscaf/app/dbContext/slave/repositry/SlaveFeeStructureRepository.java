package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFeeStructureEntity;

import java.util.UUID;

@Repository
public interface SlaveFeeStructureRepository extends ReactiveCrudRepository<SlaveFeeStructureEntity, Long> {
    Flux<SlaveFeeStructureEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveFeeStructureEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveFeeStructureEntity> findByIdAndDeletedAtIsNull(Long id);
}
