package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.EnrollmentStatusEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentStatusRepository extends ReactiveCrudRepository<EnrollmentStatusEntity, Long> {
    Mono<EnrollmentStatusEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<EnrollmentStatusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<EnrollmentStatusEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
