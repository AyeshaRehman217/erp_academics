package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildProfileEntity;

import java.util.UUID;

@Repository
public interface StudentChildProfileRepository extends ReactiveCrudRepository<StudentChildProfileEntity, Long> {

    Mono<StudentChildProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildProfileEntity> findFirstByStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(UUID childUUID, UUID uuid);

    Mono<StudentChildProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    //Check if Student Exists in Student Child Profile
    Mono<StudentChildProfileEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID stdProfileUUID);

    Mono<StudentChildProfileEntity> findFirstByNicAndStudentChildUUIDAndDeletedAtIsNull(String nic, UUID stdUuid);

    Mono<StudentChildProfileEntity> findFirstByNicAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdUuid, UUID uuid);

    Mono<StudentChildProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<StudentChildProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);
}
