package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianProfileRepository extends ReactiveCrudRepository<StudentGuardianProfileEntity, Long> {
    Mono<StudentGuardianProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    //Check if nic Exists in Student Guardian
    Mono<StudentGuardianProfileEntity> findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNull(String nic,UUID stdGuardianUUID);

    Mono<StudentGuardianProfileEntity> findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(String nic,UUID stdGuardianUUID, UUID uuid);

    Mono<StudentGuardianProfileEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianUUID);

    Mono<StudentGuardianProfileEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdGuardianUUID, UUID uuid);

//    Mono<StudentGuardianProfileEntity> findFirstByStudentGuardianDocumentUUIDAndDeletedAtIsNull(UUID studentGuardianDocumentUUID);

}
