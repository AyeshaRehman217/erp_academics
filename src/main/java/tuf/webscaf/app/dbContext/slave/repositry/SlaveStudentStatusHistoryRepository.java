package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentStatusHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentStatusHistoryRepository extends ReactiveCrudRepository<SlaveStudentStatusHistoryEntity, Long> {
    Flux<SlaveStudentStatusHistoryEntity> findAllBySubjectContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String subject);

    Mono<Long> countBySubjectContainingIgnoreCaseAndDeletedAtIsNull(String subject);

    Mono<SlaveStudentStatusHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentStatusHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
