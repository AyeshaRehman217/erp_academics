package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentStatusHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentStatusHistoryRepository extends ReactiveCrudRepository<StudentStatusHistoryEntity, Long> {
    Mono<StudentStatusHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentStatusHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentStatusHistoryEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentStatusHistoryEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndIdIsNot(UUID studentUUID,Long id);

}
