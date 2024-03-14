package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherProfileEntity;

import java.util.UUID;

@Repository
public interface StudentFatherProfileRepository extends ReactiveCrudRepository<StudentFatherProfileEntity, Long> {
    Mono<StudentFatherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherProfileEntity> findFirstByNicAndStudentFatherUUIDAndDeletedAtIsNull(String nic, UUID stdFather);

    Mono<StudentFatherProfileEntity> findFirstByNicAndStudentFatherUUIDAndDeletedAtIsNullAndUuidIsNot(String nic, UUID stdFather, UUID uuid);

    Mono<StudentFatherProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Mono<StudentFatherProfileEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdFatherUuid, UUID uuid);

    //Check if Student Profile Exists in Student Father Profile
    Mono<StudentFatherProfileEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID stdFatherUUID);

}
