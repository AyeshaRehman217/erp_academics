package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildAddressEntity;

import java.util.UUID;

@Repository
public interface StudentChildAddressRepository extends ReactiveCrudRepository<StudentChildAddressEntity, Long> {
    Mono<StudentChildAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentChildAddressEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<StudentChildAddressEntity> findFirstByStudentChildUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID studentChildUUID, UUID addressTypeUUID);

    Mono<StudentChildAddressEntity> findFirstByStudentChildUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentChildUUID,UUID addressTypeUUID, UUID uuid);
}
