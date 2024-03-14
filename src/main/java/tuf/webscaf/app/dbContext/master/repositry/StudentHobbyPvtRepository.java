package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentHobbyPvtRepository extends ReactiveCrudRepository<StudentHobbyPvtEntity, Long> {
    Flux<StudentHobbyPvtEntity> findAllByStudentUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID studentUUID, List<UUID> hobbyUUID);

    Flux<StudentHobbyPvtEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentHobbyPvtEntity> findFirstByStudentUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID studentUUID, UUID hobbyUUID);

    Mono<StudentHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentHobbyPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);
}
