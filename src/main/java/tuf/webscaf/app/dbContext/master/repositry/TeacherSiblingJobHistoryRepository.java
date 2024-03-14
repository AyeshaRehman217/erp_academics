package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingJobHistoryRepository extends ReactiveCrudRepository<TeacherSiblingJobHistoryEntity, Long> {
    Mono<TeacherSiblingJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingJobHistoryEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
