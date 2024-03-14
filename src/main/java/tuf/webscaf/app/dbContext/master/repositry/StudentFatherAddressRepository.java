package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherAddressEntity;

import java.util.UUID;

@Repository
public interface StudentFatherAddressRepository extends ReactiveCrudRepository<StudentFatherAddressEntity, Long> {
    Mono<StudentFatherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentFatherAddressEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);

    Mono<StudentFatherAddressEntity> findFirstByStudentFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID stdFatherUUID, UUID addressTypeUUID);

    Mono<StudentFatherAddressEntity> findFirstByStudentFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdFatherUUID,UUID addressTypeUUID, UUID uuid);
}
