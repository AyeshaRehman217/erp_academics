package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherFinancialHistoryRepository extends ReactiveCrudRepository<TeacherFatherFinancialHistoryEntity, Long> {
    Mono<TeacherFatherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Teacher Father Profile is used by Financial History
    Mono<TeacherFatherFinancialHistoryEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
