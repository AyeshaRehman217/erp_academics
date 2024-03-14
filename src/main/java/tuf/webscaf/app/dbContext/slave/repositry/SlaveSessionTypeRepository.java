package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSessionTypeEntity;

import java.util.UUID;

@Repository
public interface SlaveSessionTypeRepository extends ReactiveCrudRepository<SlaveSessionTypeEntity, Long> {
    Mono<SlaveSessionTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSessionTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveSessionTypeEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, Pageable pageable);

    Flux<SlaveSessionTypeEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveSessionTypeEntity> findAllByNameContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNull(String name, Boolean isSpecial, String description, Boolean isSpecial2, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndIsSpecialAndDeletedAtIsNull(String name, Boolean isSpecial, String description, Boolean isSpecial2);

    Flux<SlaveSessionTypeEntity> findAllByNameContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNull(String name, Boolean status, Boolean isSpecial, String description, Boolean status2, Boolean isSpecial2, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndIsSpecialAndDeletedAtIsNull(String name, Boolean status, Boolean isSpecial, String description, Boolean status2, Boolean isSpecial2);

    //for seeder
    Mono<SlaveSessionTypeEntity> findByNameAndDeletedAtIsNull(String name);
}
