package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingHobbyPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface TeacherSiblingHobbyPvtRepository extends ReactiveCrudRepository<TeacherSiblingHobbyPvtEntity, Long> {
    Mono<TeacherSiblingHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherSiblingHobbyPvtEntity> findAllByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Flux<TeacherSiblingHobbyPvtEntity> findAllByTeacherSiblingUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherSiblingUUID, List<UUID> ids);

    Mono<TeacherSiblingHobbyPvtEntity> findFirstByTeacherSiblingUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID, UUID hobbyUUID);

    Mono<TeacherSiblingHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherSiblingHobbyPvtEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
