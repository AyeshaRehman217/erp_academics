package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianFinancialHistoryRepository extends ReactiveCrudRepository<StudentGuardianFinancialHistoryEntity, Long> {
    Mono<StudentGuardianFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianFinancialHistoryEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
