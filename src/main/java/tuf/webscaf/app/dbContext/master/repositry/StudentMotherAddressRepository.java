package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherAddressEntity;

import java.util.UUID;

@Repository
public interface StudentMotherAddressRepository extends ReactiveCrudRepository<StudentMotherAddressEntity, Long> {
    Mono<StudentMotherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentMotherAddressEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);

    Mono<StudentMotherAddressEntity> findFirstByStudentMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID stdMotherUUID, UUID addressTypeUUID);

    Mono<StudentMotherAddressEntity> findFirstByStudentMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdMotherUUID,UUID addressTypeUUID,UUID uuid);
}
