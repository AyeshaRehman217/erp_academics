package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherJobHistoryRepository extends ReactiveCrudRepository<TeacherJobHistoryEntity, Long> {

    Mono<TeacherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherJobHistoryEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
