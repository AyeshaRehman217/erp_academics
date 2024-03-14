package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDataCollectionConfigEntity;

import java.util.UUID;

@Repository
public interface SlaveDataCollectionConfigRepository extends ReactiveCrudRepository<SlaveDataCollectionConfigEntity, Long> {
    Flux<SlaveDataCollectionConfigEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveDataCollectionConfigEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveDataCollectionConfigEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveDataCollectionConfigEntity> findAllByKeyContainingIgnoreCaseAndDeletedAtIsNullOrValueContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String key, String value);

    Flux<SlaveDataCollectionConfigEntity> findAllByKeyContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrValueContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String key, Boolean status, String value, Boolean status2);

    Mono<Long> countByKeyContainingIgnoreCaseAndDeletedAtIsNullOrValueContainingIgnoreCaseAndDeletedAtIsNull(String key, String value);

    Mono<Long> countByKeyContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrValueContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String key, Boolean status, String value, Boolean status2);
}
