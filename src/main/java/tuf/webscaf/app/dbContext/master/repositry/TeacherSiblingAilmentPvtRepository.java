package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingAilmentPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface TeacherSiblingAilmentPvtRepository extends ReactiveCrudRepository<TeacherSiblingAilmentPvtEntity, Long> {
    Mono<TeacherSiblingAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<TeacherSiblingAilmentPvtEntity> findAllByTeacherSiblingUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherSiblingUUID, List<UUID> ailmentUUID);

    Flux<TeacherSiblingAilmentPvtEntity> findAllByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingAilmentPvtEntity> findFirstByTeacherSiblingUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID, UUID ailmentUUID);

    Mono<TeacherSiblingAilmentPvtEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
