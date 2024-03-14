package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherChildFinancialHistoryRepository extends ReactiveCrudRepository<TeacherChildFinancialHistoryEntity, Long> {
    Mono<TeacherChildFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildFinancialHistoryEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
