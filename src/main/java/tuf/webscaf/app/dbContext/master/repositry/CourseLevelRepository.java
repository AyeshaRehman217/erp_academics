package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseLevelEntity;

import java.util.UUID;

@Repository
public interface CourseLevelRepository extends ReactiveCrudRepository<CourseLevelEntity, Long> {
    Mono<CourseLevelEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseLevelEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CourseLevelEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<CourseLevelEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<CourseLevelEntity> findFirstByShortNameIgnoreCaseAndDeletedAtIsNull(String shortName);

    Mono<CourseLevelEntity> findFirstByShortNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String shortName, UUID uuid);
}
