package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherProfileEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingProfileEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingProfileRepository extends ReactiveCrudRepository<StudentSiblingProfileEntity, Long> {
    Mono<StudentSiblingProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

//    Mono<StudentSiblingProfileEntity> findFirstBySiblingStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentSiblingProfileEntity> findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNull(String nic, UUID stdUuid);

    Mono<StudentSiblingProfileEntity> findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdUuid, UUID uuid);

    Mono<StudentSiblingProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<StudentSiblingProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    //Check if Student  Exists in Student Sibling Profile
    Mono<StudentSiblingProfileEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID stdFatherUUID);

    Mono<StudentSiblingProfileEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdFatherUUID, UUID uuid);

}
