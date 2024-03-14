package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.GuardianTypeEntity;

import java.util.UUID;

@Repository
public interface GuardianTypeRepository extends ReactiveCrudRepository<GuardianTypeEntity, Long> {
    Mono<GuardianTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<GuardianTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<GuardianTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<GuardianTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name,UUID uuid);

    Mono<GuardianTypeEntity> findFirstBySlugAndDeletedAtIsNull(String slug);

    Mono<GuardianTypeEntity> findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(String slug,UUID uuid);
}
