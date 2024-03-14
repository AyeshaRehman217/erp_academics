package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherChildJobHistoryRepository extends ReactiveCrudRepository<TeacherChildJobHistoryEntity, Long> {
    Mono<TeacherChildJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildJobHistoryEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
