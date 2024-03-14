package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseBatchEntity;

import java.util.UUID;

@Repository
public interface CourseBatchRepository extends ReactiveCrudRepository<CourseBatchEntity, Long> {
    Mono<CourseBatchEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseBatchEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CourseBatchEntity> findFirstByCodeAndDeletedAtIsNull(String code);

    Mono<CourseBatchEntity> findFirstByCodeAndDeletedAtIsNullAndIdIsNot(String code, Long id);

    Mono<CourseBatchEntity> findFirstByCourseOfferedUUIDAndDeletedAtIsNull(UUID courseOfferedUUID);
}
