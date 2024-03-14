package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentJobHistoryRepository extends ReactiveCrudRepository<StudentJobHistoryEntity, Long> {
    Mono<StudentJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Student  Exists in Student Job History
    Mono<StudentJobHistoryEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

}
