package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherChildAilmentPvtRepository extends ReactiveCrudRepository<TeacherChildAilmentPvtEntity, Long> {
    Mono<TeacherChildAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<TeacherChildAilmentPvtEntity> findAllByTeacherChildUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> ailmentUUID);

    Flux<TeacherChildAilmentPvtEntity> findAllByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildAilmentPvtEntity> findFirstByTeacherChildUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID ailmentUUID);

    Mono<TeacherChildAilmentPvtEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
