package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherJobHistoryRepository extends ReactiveCrudRepository<TeacherFatherJobHistoryEntity, Long> {
    Mono<TeacherFatherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Teacher Father is used by Father Job History
    Mono<TeacherFatherJobHistoryEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
