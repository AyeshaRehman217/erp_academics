package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentChildAilmentPvtRepository extends ReactiveCrudRepository<StudentChildAilmentPvtEntity, Long> {

    Mono<StudentChildAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<StudentChildAilmentPvtEntity> findAllByStudentChildUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentChildUUID, List<UUID> ailmentUUID);

    Flux<StudentChildAilmentPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<StudentChildAilmentPvtEntity> findFirstByStudentChildUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentChildUUID, UUID ailmentUUID);

    Mono<StudentChildAilmentPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);
}
