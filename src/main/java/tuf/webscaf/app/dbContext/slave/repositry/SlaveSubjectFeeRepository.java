package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectFeeEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectFeeRepository extends ReactiveCrudRepository<SlaveSubjectFeeEntity, Long> {
    Mono<SlaveSubjectFeeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectFeeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveSubjectFeeEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Flux<SlaveSubjectFeeEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    //Find By Currency uuid In Config Module
    Mono<SlaveSubjectFeeEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
