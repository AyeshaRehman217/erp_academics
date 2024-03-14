package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherChildProfileRepository extends ReactiveCrudRepository<TeacherChildProfileEntity, Long> {
    Mono<TeacherChildProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    Mono<TeacherChildProfileEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildProfileEntity> findFirstByTeacherChildUUIDAndNicAndDeletedAtIsNull(UUID teacherChildUUID, String nic);

    Mono<TeacherChildProfileEntity> findFirstByTeacherChildUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherChildUUID, String nic, UUID uuid);

    Mono<TeacherChildProfileEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherChildUUID, UUID uuid);

    Mono<TeacherChildProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherChildProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherChildProfileEntity> findFirstByNicAndTeacherChildUUIDAndDeletedAtIsNull(String nic, UUID teacherChildUUID);

    Mono<TeacherChildProfileEntity> findFirstByNicAndTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID teacherChildUUID, UUID uuid);
}
