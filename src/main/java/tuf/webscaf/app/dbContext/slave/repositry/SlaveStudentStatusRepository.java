package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentStatusEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentStatusRepository extends ReactiveCrudRepository<SlaveStudentStatusEntity, Long> {
    Flux<SlaveStudentStatusEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String description,Pageable pageable);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String description);

    Mono<SlaveStudentStatusEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentStatusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
