package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildEntity;

import java.util.UUID;

@Repository
public interface StudentChildRepository extends ReactiveCrudRepository<StudentChildEntity, Long> {

    Mono<StudentChildEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentChildEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<StudentChildEntity> findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNull(UUID studentUUID, UUID studentChildUUID);

    Mono<StudentChildEntity> findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID studentChildUUID, UUID uuid);
}
