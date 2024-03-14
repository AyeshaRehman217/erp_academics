package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DegreeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DegreeRepository extends ReactiveCrudRepository<DegreeEntity, Long> {
    Mono<DegreeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DegreeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<DegreeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Mono<DegreeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<DegreeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

}
