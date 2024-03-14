package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherHobbyPvtRepository extends ReactiveCrudRepository<TeacherHobbyPvtEntity, Long> {
    Mono<TeacherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherHobbyPvtEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Flux<TeacherHobbyPvtEntity> findAllByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Flux<TeacherHobbyPvtEntity> findAllByTeacherUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherUUID, List<UUID> ids);

    Mono<TeacherHobbyPvtEntity> findFirstByTeacherUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID hobbyUUID);
}
