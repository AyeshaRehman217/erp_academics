package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseJobHistoryRepository extends ReactiveCrudRepository<StudentSpouseJobHistoryEntity, Long> {
    Mono<StudentSpouseJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseJobHistoryEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
