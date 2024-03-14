package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingJobHistoryRepository extends ReactiveCrudRepository<StudentSiblingJobHistoryEntity, Long> {
    Mono<StudentSiblingJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingJobHistoryEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);
}
