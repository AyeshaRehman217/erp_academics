package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherProfileEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherProfileRepository extends ReactiveCrudRepository<TeacherMotherProfileEntity, Long> {
    Mono<TeacherMotherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<TeacherMotherProfileEntity> findFirstByTeacherMotherDocumentUUIDAndDeletedAtIsNull(UUID teacherMotherDocumentUUID);

    Mono<TeacherMotherProfileEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherProfileEntity> findFirstByTeacherMotherUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(UUID teacherMotherUUID, String nic, UUID uuid);

    Mono<TeacherMotherProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<TeacherMotherProfileEntity> findFirstByTeacherMotherUUIDAndNicAndDeletedAtIsNull(UUID teacherMotherUUID, String nic);

    Mono<TeacherMotherProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<TeacherMotherProfileEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID mthUUID,UUID uuid);

    Mono<TeacherMotherProfileEntity> findFirstByNicAndTeacherMotherUUIDAndDeletedAtIsNull(String nic, UUID stdMother);

    Mono<TeacherMotherProfileEntity> findFirstByNicAndTeacherMotherUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdMother, UUID uuid);


}
