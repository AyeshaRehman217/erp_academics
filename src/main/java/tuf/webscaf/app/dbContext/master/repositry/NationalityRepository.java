package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface NationalityRepository extends ReactiveCrudRepository<NationalityEntity, Long> {
    Mono<NationalityEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<NationalityEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<NationalityEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);

    Mono<NationalityEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<NationalityEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name,UUID uuid);
}
