package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSiblingAilmentPvtRepository extends ReactiveCrudRepository<StudentSiblingAilmentPvtEntity, Long> {

    Mono<StudentSiblingAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentSiblingAilmentPvtEntity> findAllByStudentSiblingUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentSiblingUUID, UUID ailmentUUID);

    Flux<StudentSiblingAilmentPvtEntity> findAllByStudentSiblingUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentSiblingUUID, List<UUID> ailmentUUID);

    Flux<StudentSiblingAilmentPvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

    Mono<StudentSiblingAilmentPvtEntity> findFirstByStudentSiblingUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentSiblingUUID, UUID ailmentUUID);

    Mono<StudentSiblingAilmentPvtEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);

}
