package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingRepository extends ReactiveCrudRepository<StudentSiblingEntity, Long> {
    Mono<StudentSiblingEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUuid);

    Mono<StudentSiblingEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdUuid, UUID uuid);

    Mono<StudentSiblingEntity> findByUuidAndStudentUUIDAndDeletedAtIsNull(UUID uuid, UUID studentUUID);

    Mono<StudentSiblingEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

    Mono<StudentSiblingEntity> findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNull(UUID studentUUID, UUID studentSiblingUUID);

    Mono<StudentSiblingEntity> findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID studentSiblingUUID, UUID uuid);
}
