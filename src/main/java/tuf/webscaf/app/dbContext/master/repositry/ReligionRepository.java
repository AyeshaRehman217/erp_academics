package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.ReligionEntity;

import java.util.UUID;

@Repository
public interface ReligionRepository extends ReactiveCrudRepository<ReligionEntity, Long> {
    Mono<ReligionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<ReligionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<ReligionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<ReligionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name,UUID uuid);
}
