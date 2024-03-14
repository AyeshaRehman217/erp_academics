package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherProfileRepository extends ReactiveCrudRepository<TeacherProfileEntity, Long> {
    Mono<TeacherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<TeacherProfileEntity> findFirstByTeacherDocumentUUIDAndDeletedAtIsNull(UUID teacherDocumentUUID);

    Mono<TeacherProfileEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherProfileEntity> findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID uuid);

    Mono<TeacherProfileEntity> findFirstByCasteUUIDAndDeletedAtIsNull(UUID casteUUID);

    Mono<TeacherProfileEntity> findFirstByReligionUUIDAndDeletedAtIsNull(UUID religionUUID);

    Mono<TeacherProfileEntity> findFirstByGenderUUIDAndDeletedAtIsNull(UUID genderUUID);

    Mono<TeacherProfileEntity> findFirstByMaritalStatusUUIDAndDeletedAtIsNull(UUID maritalStatusUUID);

    //Check if Sect UUID exists in Teacher Profile
    Mono<TeacherProfileEntity> findFirstBySectUUIDAndDeletedAtIsNull(UUID sectUUID);

    Mono<TeacherProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherProfileEntity> findFirstByTeacherUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, String nic, UUID uuid);

    Mono<TeacherProfileEntity> findFirstByTeacherUUIDAndNicAndDeletedAtIsNull(UUID teacherUUID, String nic);
}
