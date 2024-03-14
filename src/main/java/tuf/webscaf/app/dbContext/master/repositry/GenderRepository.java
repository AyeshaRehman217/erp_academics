package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.GenderEntity;

import java.util.UUID;

@Repository
public interface GenderRepository extends ReactiveCrudRepository<GenderEntity, Long> {
    Mono<GenderEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<GenderEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<GenderEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<GenderEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
