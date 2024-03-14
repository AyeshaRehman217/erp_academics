package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherAilmentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentMotherAilmentPvtRepository extends ReactiveCrudRepository<StudentMotherAilmentPvtEntity, Long> {
    Mono<StudentMotherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherAilmentPvtEntity> findFirstByAilmentUUIDAndDeletedAtIsNull(UUID ailmentUUID);

    Mono<StudentMotherAilmentPvtEntity> findAllByStudentMotherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentMotherUUID, UUID ailmentUUID);

    Flux<StudentMotherAilmentPvtEntity> findAllByStudentMotherUUIDAndAilmentUUIDInAndDeletedAtIsNull(UUID studentMotherUUID, List<UUID> ailmentUUID);

    Flux<StudentMotherAilmentPvtEntity> findAllByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);

    Mono<StudentMotherAilmentPvtEntity> findFirstByStudentMotherUUIDAndAilmentUUIDAndDeletedAtIsNull(UUID studentMotherUUID, UUID ailmentUUID);

    Mono<StudentMotherAilmentPvtEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);
}
