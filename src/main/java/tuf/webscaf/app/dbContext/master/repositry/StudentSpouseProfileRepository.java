package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseProfileEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseProfileRepository extends ReactiveCrudRepository<StudentSpouseProfileEntity, Long> {
    Mono<StudentSpouseProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Student Spouse Exists in Student Spouse Profile
    Mono<StudentSpouseProfileEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseProfileEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, UUID uuid);

    //Check if nic Exists in Student Spouse Profile
    Mono<StudentSpouseProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    //Check if nic Exists in Student Spouse Profile
    Mono<StudentSpouseProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<StudentSpouseProfileEntity> findFirstByNicAndStudentSpouseUUIDAndDeletedAtIsNull(String nic, UUID teacherSpouseUUID);

    Mono<StudentSpouseProfileEntity> findFirstByNicAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID teacherSpouseUUID, UUID uuid);
}
