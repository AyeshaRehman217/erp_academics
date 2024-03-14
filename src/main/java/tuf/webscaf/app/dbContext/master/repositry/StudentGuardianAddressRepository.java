package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianAddressEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianAddressRepository extends ReactiveCrudRepository<StudentGuardianAddressEntity, Long> {
    Mono<StudentGuardianAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentGuardianAddressEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);

    Mono<StudentGuardianAddressEntity> findFirstByStudentGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID stdGuardianUUID, UUID addressTypeUUID);

    Mono<StudentGuardianAddressEntity> findFirstByStudentGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdGuardianUUID,UUID addressTypeUUID, UUID uuid);
}
