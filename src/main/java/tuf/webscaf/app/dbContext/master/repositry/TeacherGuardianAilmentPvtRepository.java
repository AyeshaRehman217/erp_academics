package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherGuardianAilmentPvtRepository extends ReactiveCrudRepository<TeacherGuardianAilmentPvtEntity, Long> {
    Mono<TeacherGuardianAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<TeacherGuardianAilmentPvtEntity> findAllByTeacherGuardianUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID ailmentUUID);

    Flux<TeacherGuardianAilmentPvtEntity> findAllByTeacherGuardianUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherGuardianUUID, List<UUID> ailmentUUID);

    Flux<TeacherGuardianAilmentPvtEntity> findAllByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianAilmentPvtEntity> findFirstByTeacherGuardianUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID ailmentUUID);

    Mono<TeacherGuardianAilmentPvtEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
