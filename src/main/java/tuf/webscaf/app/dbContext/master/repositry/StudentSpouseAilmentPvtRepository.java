package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSpouseAilmentPvtRepository extends ReactiveCrudRepository<StudentSpouseAilmentPvtEntity, Long> {
    Mono<StudentSpouseAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentSpouseAilmentPvtEntity> findAllByStudentSpouseUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID ailmentUUID);

    Flux<StudentSpouseAilmentPvtEntity> findAllByStudentSpouseUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> ailmentUUID);

    Flux<StudentSpouseAilmentPvtEntity> findAllByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseAilmentPvtEntity> findFirstByStudentSpouseUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID ailmentUUID);

    Mono<StudentSpouseAilmentPvtEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
