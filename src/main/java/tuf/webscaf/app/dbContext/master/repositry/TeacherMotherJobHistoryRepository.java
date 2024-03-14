package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherJobHistoryRepository extends ReactiveCrudRepository<TeacherMotherJobHistoryEntity, Long> {

    Mono<TeacherMotherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherJobHistoryEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
