package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseProfileRepository extends ReactiveCrudRepository<TeacherSpouseProfileEntity, Long> {
    Mono<TeacherSpouseProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Teacher Spouse Exists in Teacher Spouse Profile
    Mono<TeacherSpouseProfileEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseProfileEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, UUID uuid);

    //Check if nic Exists in Teacher Spouse Profile
    Mono<TeacherSpouseProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    //Check if nic Exists in Teacher Spouse Profile
    Mono<TeacherSpouseProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherSpouseProfileEntity> findFirstByNicAndTeacherSpouseUUIDAndDeletedAtIsNull(String nic, UUID teacherSpouseUUID);

    Mono<TeacherSpouseProfileEntity> findFirstByNicAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID teacherSpouseUUID, UUID uuid);
}
