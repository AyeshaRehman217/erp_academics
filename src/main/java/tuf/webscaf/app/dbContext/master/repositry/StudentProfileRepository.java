package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentProfileEntity;

import java.util.UUID;

@Repository
public interface StudentProfileRepository extends ReactiveCrudRepository<StudentProfileEntity, Long> {

    Mono<StudentProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<StudentProfileEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<StudentProfileEntity> findFirstByCasteUUIDAndDeletedAtIsNull(UUID casteUUID);

    Mono<StudentProfileEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID uuid);

    Mono<StudentProfileEntity> findFirstByReligionUUIDAndDeletedAtIsNull(UUID religionUUID);

    Mono<StudentProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    Mono<StudentProfileEntity> findFirstByMaritalStatusUUIDAndDeletedAtIsNull(UUID maritalStatusUUID);

    //Check if Sect UUID exists in Student Profile
    Mono<StudentProfileEntity> findFirstBySectUUIDAndDeletedAtIsNull(UUID sectUUID);

}
