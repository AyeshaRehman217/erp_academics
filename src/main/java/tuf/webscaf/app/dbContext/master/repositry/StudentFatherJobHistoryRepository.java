package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentFatherJobHistoryRepository extends ReactiveCrudRepository<StudentFatherJobHistoryEntity, Long> {
    Mono<StudentFatherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherJobHistoryEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);
}
