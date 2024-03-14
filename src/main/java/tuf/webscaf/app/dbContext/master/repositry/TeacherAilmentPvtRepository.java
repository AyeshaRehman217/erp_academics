package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherAilmentPvtRepository extends ReactiveCrudRepository<TeacherAilmentPvtEntity, Long> {
    Mono<TeacherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<TeacherAilmentPvtEntity> findAllByTeacherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentUUID, UUID ailmentUUID);

    Flux<TeacherAilmentPvtEntity> findAllByTeacherUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentUUID, List<UUID> ailmentUUID);

    Flux<TeacherAilmentPvtEntity> findAllByTeacherUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<TeacherAilmentPvtEntity> findFirstByTeacherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentUUID, UUID ailmentUUID);

    Mono<TeacherAilmentPvtEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
