package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherProfileRepository extends ReactiveCrudRepository<TeacherFatherProfileEntity, Long> {
    Mono<TeacherFatherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherProfileEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<TeacherFatherProfileEntity> findFirstByTeacherFatherUUIDAndNicAndDeletedAtIsNull(UUID teacherFatherUUID, String nic);

    Mono<TeacherFatherProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherFatherProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherFatherProfileEntity> findFirstByTeacherFatherUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherFatherUUID, String nic, UUID uuid);

    Mono<TeacherFatherProfileEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherFatherUUID, UUID uuid);

    Mono<TeacherFatherProfileEntity> findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNull(String nic, UUID stdFather);

    Mono<TeacherFatherProfileEntity> findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdFather, UUID uuid);
}
