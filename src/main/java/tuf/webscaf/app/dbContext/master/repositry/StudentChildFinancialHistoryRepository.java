package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentChildFinancialHistoryRepository extends ReactiveCrudRepository<StudentChildFinancialHistoryEntity, Long> {
    
    Mono<StudentChildFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildFinancialHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
