package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSpouseAilmentPvtRepository extends ReactiveCrudRepository<TeacherSpouseAilmentPvtEntity, Long> {
    Mono<TeacherSpouseAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<TeacherSpouseAilmentPvtEntity> findAllByTeacherSpouseUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID ailmentUUID);

    Flux<TeacherSpouseAilmentPvtEntity> findAllByTeacherSpouseUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> ailmentUUID);

    Flux<TeacherSpouseAilmentPvtEntity> findAllByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseAilmentPvtEntity> findFirstByTeacherSpouseUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID ailmentUUID);

    Mono<TeacherSpouseAilmentPvtEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
