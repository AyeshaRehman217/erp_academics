package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseJobHistoryRepository extends ReactiveCrudRepository<TeacherSpouseJobHistoryEntity, Long> {
    Mono<TeacherSpouseJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseJobHistoryEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
