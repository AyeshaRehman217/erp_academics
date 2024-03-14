package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentFatherFinancialHistoryRepository extends ReactiveCrudRepository<StudentFatherFinancialHistoryEntity, Long> {
    Mono<StudentFatherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherFinancialHistoryEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);
}
