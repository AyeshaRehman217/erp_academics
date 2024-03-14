package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherFatherHobbyPvtRepository extends ReactiveCrudRepository<TeacherFatherHobbyPvtEntity, Long> {
    Mono<TeacherFatherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherFatherHobbyPvtEntity> findAllByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Flux<TeacherFatherHobbyPvtEntity> findAllByTeacherFatherUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherFatherUUID, List<UUID> ids);

    Mono<TeacherFatherHobbyPvtEntity> findFirstByTeacherFatherUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherFatherUUID, UUID hobbyUUID);

    Mono<TeacherFatherHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    //Check if Teacher Father is used by Teacher Father Hobbies Pvt
    Mono<TeacherFatherHobbyPvtEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
