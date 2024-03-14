package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.MaritalStatusEntity;

import java.util.UUID;

@Repository
public interface MaritalStatusRepository extends ReactiveCrudRepository<MaritalStatusEntity, Long> {
    Mono<MaritalStatusEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<MaritalStatusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<MaritalStatusEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<MaritalStatusEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
