package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherFatherAilmentPvtRepository extends ReactiveCrudRepository<TeacherFatherAilmentPvtEntity, Long> {
    Mono<TeacherFatherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<TeacherFatherAilmentPvtEntity> findAllByTeacherFatherUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherFatherUUID, List<UUID> ailmentUUID);

    Flux<TeacherFatherAilmentPvtEntity> findAllByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<TeacherFatherAilmentPvtEntity> findFirstByTeacherFatherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherFatherUUID, UUID ailmentUUID);

    //Check if Teacher Father is used by Teacher Father Ailments Pvt
    Mono<TeacherFatherAilmentPvtEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
