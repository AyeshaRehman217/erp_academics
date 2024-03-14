package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface HobbyRepository extends ReactiveCrudRepository<HobbyEntity, Long>{
    Mono<HobbyEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<HobbyEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<HobbyEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);

    Mono<HobbyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<HobbyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
