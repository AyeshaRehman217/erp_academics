package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectFeeEntity;

import java.util.UUID;

@Repository
public interface SubjectFeeRepository extends ReactiveCrudRepository<SubjectFeeEntity, Long> {
    Mono<SubjectFeeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectFeeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectFeeEntity> findFirstBySubjectOfferedUUIDAndDeletedAtIsNull(UUID subjectOfferedUUID);
}
