package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentFinancialHistoryRepository extends ReactiveCrudRepository<StudentFinancialHistoryEntity, Long> {
    Mono<StudentFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFinancialHistoryEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);
}
