package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianJobHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianJobHistoryRepository extends ReactiveCrudRepository<TeacherGuardianJobHistoryEntity, Long> {
    Mono<TeacherGuardianJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianJobHistoryEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
