package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherProfileEntity;

import java.util.UUID;

@Repository
public interface StudentMotherProfileRepository extends ReactiveCrudRepository<StudentMotherProfileEntity, Long> {
    Mono<StudentMotherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherProfileEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdUUID,UUID uuid);

    //Check if Student Profile Exists in Student Mother Profile
    Mono<StudentMotherProfileEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID stdProfileUUID);

    //Check if nic Exists in Student Mother Profile
    Mono<StudentMotherProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    //Check if nic Exists in Student Mother Profile
    Mono<StudentMotherProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic,UUID uuid);

    Mono<StudentMotherProfileEntity> findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNull(String nic,UUID stdUuid);

    Mono<StudentMotherProfileEntity> findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(String nic,UUID stdUuid, UUID uuid);
}
