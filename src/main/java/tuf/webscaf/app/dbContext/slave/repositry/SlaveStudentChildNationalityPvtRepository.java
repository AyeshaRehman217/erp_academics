package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentChildNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentChildNationalityPvtEntity, Long> {
    
    Mono<SlaveStudentChildNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentChildNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<SlaveStudentChildNationalityPvtEntity> findAllByStudentChildUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> uuids);

    Flux<SlaveStudentChildNationalityPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<SlaveStudentChildNationalityPvtEntity> findFirstByStudentChildUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID nationalityUUID);

    Mono<SlaveStudentChildNationalityPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

}
