package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherMotherHobbyPvtRepository extends ReactiveCrudRepository<TeacherMotherHobbyPvtEntity, Long> {
    Mono<TeacherMotherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherMotherHobbyPvtEntity> findAllByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Flux<TeacherMotherHobbyPvtEntity> findAllByTeacherMotherUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherMotherUUID, List<UUID> ids);

    Mono<TeacherMotherHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherMotherHobbyPvtEntity> findFirstByTeacherMotherUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherMotherUUID, UUID hobbyUUID);

    Mono<TeacherMotherHobbyPvtEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
