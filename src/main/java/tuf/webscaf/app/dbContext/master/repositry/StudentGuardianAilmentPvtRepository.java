package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGuardianAilmentPvtRepository extends ReactiveCrudRepository<StudentGuardianAilmentPvtEntity, Long> {
    Mono<StudentGuardianAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentGuardianAilmentPvtEntity> findAllByStudentGuardianUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentGuardianUUID, UUID ailmentUUID);

    Flux<StudentGuardianAilmentPvtEntity> findAllByStudentGuardianUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentGuardianUUID, List<UUID> ailmentUUID);

    Flux<StudentGuardianAilmentPvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);

    Mono<StudentGuardianAilmentPvtEntity> findFirstByStudentGuardianUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentGuardianUUID, UUID ailmentUUID);

    Mono<StudentGuardianAilmentPvtEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
