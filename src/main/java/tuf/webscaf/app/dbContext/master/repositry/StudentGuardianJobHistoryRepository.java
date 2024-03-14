package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianJobHistoryRepository extends ReactiveCrudRepository<StudentGuardianJobHistoryEntity, Long> {
    Mono<StudentGuardianJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianJobHistoryEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
