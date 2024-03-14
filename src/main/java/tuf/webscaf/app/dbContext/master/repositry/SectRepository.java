package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SectEntity;

import java.util.UUID;

@Repository
public interface SectRepository extends ReactiveCrudRepository<SectEntity, Long> {
    Mono<SectEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SectEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SectEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<SectEntity> findFirstByReligionUUIDAndDeletedAtIsNull(UUID religionUUID);

}
