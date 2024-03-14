package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentChildJobHistoryRepository extends ReactiveCrudRepository<StudentChildJobHistoryEntity, Long> {

    Mono<StudentChildJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildJobHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);
}
