package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentSiblingHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentSiblingHobbyPvtEntity, Long> {
    Mono<SlaveStudentSiblingHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentSiblingHobbyPvtEntity> findAllByStudentSiblingUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID stdSiblingUUID, List<UUID> hobbyUUID);

    Flux<SlaveStudentSiblingHobbyPvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID stdSiblingUUID);
}
