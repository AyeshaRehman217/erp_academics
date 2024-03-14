package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherFinancialHistoryRepository extends ReactiveCrudRepository<TeacherMotherFinancialHistoryEntity, Long> {
    Mono<TeacherMotherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherFinancialHistoryEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
