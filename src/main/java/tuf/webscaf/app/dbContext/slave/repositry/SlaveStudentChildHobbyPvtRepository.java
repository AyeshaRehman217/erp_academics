package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentChildHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentChildHobbyPvtEntity, Long> {
    
    Mono<SlaveStudentChildHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentChildHobbyPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Flux<SlaveStudentChildHobbyPvtEntity> findAllByStudentChildUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> ids);

    Mono<SlaveStudentChildHobbyPvtEntity> findFirstByStudentChildUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID hobbyUUID);

    Mono<SlaveStudentChildHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<SlaveStudentChildHobbyPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
