package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseFinancialHistoryRepository extends ReactiveCrudRepository<StudentSpouseFinancialHistoryEntity, Long> {
    Mono<StudentSpouseFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseFinancialHistoryEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
