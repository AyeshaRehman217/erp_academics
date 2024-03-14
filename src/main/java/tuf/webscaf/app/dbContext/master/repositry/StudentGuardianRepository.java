package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianRepository extends ReactiveCrudRepository<StudentGuardianEntity, Long> {
    Mono<StudentGuardianEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentGuardianEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID uuid);

}
