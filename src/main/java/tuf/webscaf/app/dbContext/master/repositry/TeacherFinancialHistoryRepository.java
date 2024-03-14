package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherFinancialHistoryRepository extends ReactiveCrudRepository<TeacherFinancialHistoryEntity, Long> {

    Mono<TeacherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFinancialHistoryEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
