package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianProfileRepository extends ReactiveCrudRepository<TeacherGuardianProfileEntity, Long> {
    Mono<TeacherGuardianProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianProfileEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianProfileEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherGuardianUUID, UUID uuid);

    Mono<TeacherGuardianProfileEntity> findFirstByTeacherGuardianUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherGuardianUUID, String nic, UUID uuid);

    Mono<TeacherGuardianProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherGuardianProfileEntity> findFirstByTeacherGuardianUUIDAndNicAndDeletedAtIsNull(UUID teacherGuardianUUID, String nic);

    Mono<TeacherGuardianProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherGuardianProfileEntity> findFirstByNicAndTeacherGuardianUUIDAndDeletedAtIsNull(String nic, UUID stdGuardian);

    Mono<TeacherGuardianProfileEntity> findFirstByNicAndTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdGuardian, UUID uuid);

}
