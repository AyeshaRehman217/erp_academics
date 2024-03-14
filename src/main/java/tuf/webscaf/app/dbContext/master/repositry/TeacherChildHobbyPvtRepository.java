package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherChildHobbyPvtRepository extends ReactiveCrudRepository<TeacherChildHobbyPvtEntity, Long> {
    Mono<TeacherChildHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherChildHobbyPvtEntity> findAllByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Flux<TeacherChildHobbyPvtEntity> findAllByTeacherChildUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> ids);

    Mono<TeacherChildHobbyPvtEntity> findFirstByTeacherChildUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID hobbyUUID);

    Mono<TeacherChildHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherChildHobbyPvtEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
