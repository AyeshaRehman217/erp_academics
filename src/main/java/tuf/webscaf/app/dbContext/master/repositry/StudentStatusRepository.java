package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentStatusEntity;

import java.util.UUID;

@Repository
public interface StudentStatusRepository extends ReactiveCrudRepository<StudentStatusEntity, Long> {
    Mono<StudentStatusEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentStatusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentStatusEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentStatusEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndIdIsNot(UUID studentUUID,Long id);
}
