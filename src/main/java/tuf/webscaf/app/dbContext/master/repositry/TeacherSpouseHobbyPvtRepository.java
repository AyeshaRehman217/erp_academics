package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseHobbyPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface TeacherSpouseHobbyPvtRepository extends ReactiveCrudRepository<TeacherSpouseHobbyPvtEntity, Long> {
    Mono<TeacherSpouseHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseHobbyPvtEntity> findFirstByTeacherSpouseUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID hobbyUUID);

    Flux<TeacherSpouseHobbyPvtEntity> findAllByTeacherSpouseUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> ids);

    Flux<TeacherSpouseHobbyPvtEntity> findAllByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherSpouseHobbyPvtEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
