package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianHobbyPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface TeacherGuardianHobbyPvtRepository extends ReactiveCrudRepository<TeacherGuardianHobbyPvtEntity, Long> {
    Mono<TeacherGuardianHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianHobbyPvtEntity> findFirstByTeacherGuardianUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID hobbyUUID);

    Flux<TeacherGuardianHobbyPvtEntity> findAllByTeacherGuardianUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherGuardianUUID, List<UUID> ids);

    Flux<TeacherGuardianHobbyPvtEntity> findAllByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<TeacherGuardianHobbyPvtEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
