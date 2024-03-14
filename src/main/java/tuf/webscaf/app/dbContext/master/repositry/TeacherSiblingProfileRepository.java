package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingProfileRepository extends ReactiveCrudRepository<TeacherSiblingProfileEntity, Long> {

    Mono<TeacherSiblingProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    Mono<TeacherSiblingProfileEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingProfileEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSiblingUUID, UUID uuid);

    Mono<TeacherSiblingProfileEntity> findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNull(UUID teacherSiblingUUID,String nic);

    Mono<TeacherSiblingProfileEntity> findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherSiblingUUID,String nic, UUID uuid);

    Mono<TeacherSiblingProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherSiblingProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);
}
