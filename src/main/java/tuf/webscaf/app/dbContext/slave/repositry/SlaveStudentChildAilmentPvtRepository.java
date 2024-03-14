package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentChildAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentChildAilmentPvtEntity, Long> {

    Mono<SlaveStudentChildAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentChildAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Flux<SlaveStudentChildAilmentPvtEntity> findAllByStudentChildUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentChildUUID, List<UUID> ailmentUUID);

    Flux<SlaveStudentChildAilmentPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<SlaveStudentChildAilmentPvtEntity> findFirstByStudentChildUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentChildUUID, UUID ailmentUUID);

    Mono<SlaveStudentChildAilmentPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);
}
