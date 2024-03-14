package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentMotherJobHistoryRepository extends ReactiveCrudRepository<StudentMotherJobHistoryEntity, Long> {
    Mono<StudentMotherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherJobHistoryEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);
}
