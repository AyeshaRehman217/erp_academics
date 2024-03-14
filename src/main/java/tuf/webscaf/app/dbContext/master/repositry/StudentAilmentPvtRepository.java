package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentAilmentPvtRepository extends ReactiveCrudRepository<StudentAilmentPvtEntity, Long> {
    Mono<StudentAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentAilmentPvtEntity> findAllByStudentUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentUUID, UUID ailmentUUID);

    Flux<StudentAilmentPvtEntity> findAllByStudentUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentUUID, List<UUID> ailmentUUID);

    Flux<StudentAilmentPvtEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentAilmentPvtEntity> findFirstByStudentUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentUUID, UUID ailmentUUID);

    Mono<StudentAilmentPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);
}
