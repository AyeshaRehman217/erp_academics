package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentMotherFinancialHistoryRepository extends ReactiveCrudRepository<StudentMotherFinancialHistoryEntity, Long> {
    Mono<StudentMotherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherFinancialHistoryEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);
}
