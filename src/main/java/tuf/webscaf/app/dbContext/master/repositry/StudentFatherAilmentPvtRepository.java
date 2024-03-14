package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentFatherAilmentPvtRepository extends ReactiveCrudRepository<StudentFatherAilmentPvtEntity, Long> {
    Mono<StudentFatherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentFatherAilmentPvtEntity> findAllByStudentFatherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentFatherUUID, UUID ailmentUUID);

    Flux<StudentFatherAilmentPvtEntity> findAllByStudentFatherUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentFatherUUID, List<UUID> ailmentUUID);

    Flux<StudentFatherAilmentPvtEntity> findAllByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);

    Mono<StudentFatherAilmentPvtEntity> findFirstByStudentFatherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentFatherUUID, UUID ailmentUUID);

    Mono<StudentFatherAilmentPvtEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);
}
