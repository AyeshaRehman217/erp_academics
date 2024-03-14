package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherMotherAilmentPvtRepository extends ReactiveCrudRepository<TeacherMotherAilmentPvtEntity, Long> {
    Mono<TeacherMotherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<TeacherMotherAilmentPvtEntity> findAllByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Flux<TeacherMotherAilmentPvtEntity> findAllByTeacherMotherUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherMotherUUID, List<UUID> ailmentUUID);

    Flux<TeacherMotherAilmentPvtEntity> findByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherAilmentPvtEntity> findFirstByTeacherMotherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherMotherUUID, UUID ailmentUUID);

    Mono<TeacherMotherAilmentPvtEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
