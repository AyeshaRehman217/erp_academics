package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingFinancialHistoryRepository extends ReactiveCrudRepository<TeacherSiblingFinancialHistoryEntity, Long> {
    Mono<TeacherSiblingFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingFinancialHistoryEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
