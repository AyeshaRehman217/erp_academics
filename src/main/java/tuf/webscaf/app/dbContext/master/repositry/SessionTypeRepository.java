package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SessionTypeEntity;

import java.util.UUID;

@Repository
public interface SessionTypeRepository extends ReactiveCrudRepository<SessionTypeEntity, Long> {

    Mono<SessionTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SessionTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SessionTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SessionTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
