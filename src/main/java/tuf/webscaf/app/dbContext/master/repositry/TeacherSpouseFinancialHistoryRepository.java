package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseFinancialHistoryRepository extends ReactiveCrudRepository<TeacherSpouseFinancialHistoryEntity, Long> {
    Mono<TeacherSpouseFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseFinancialHistoryEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
