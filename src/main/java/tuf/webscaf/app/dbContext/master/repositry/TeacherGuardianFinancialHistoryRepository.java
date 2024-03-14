package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianFinancialHistoryRepository extends ReactiveCrudRepository<TeacherGuardianFinancialHistoryEntity, Long> {
    Mono<TeacherGuardianFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianFinancialHistoryEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
