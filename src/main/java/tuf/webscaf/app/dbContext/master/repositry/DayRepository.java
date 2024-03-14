package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DayEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DayRepository extends ReactiveCrudRepository<DayEntity, Long> {
    Mono<DayEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DayEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<DayEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<DayEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<DayEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
