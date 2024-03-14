package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingFinancialHistoryRepository extends ReactiveCrudRepository<StudentSiblingFinancialHistoryEntity, Long> {

    Mono<StudentSiblingFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingFinancialHistoryEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);
}
