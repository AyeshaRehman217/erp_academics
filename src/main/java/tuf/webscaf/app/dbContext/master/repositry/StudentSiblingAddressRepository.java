package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingAddressEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingAddressRepository extends ReactiveCrudRepository<StudentSiblingAddressEntity, Long> {
    Mono<StudentSiblingAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentSiblingAddressEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);
    
    Mono<StudentSiblingAddressEntity> findFirstByStudentSiblingUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID stdSiblingUUID, UUID addressTypeUUID);

    Mono<StudentSiblingAddressEntity> findFirstByStudentSiblingUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID stdSiblingUUID,UUID addressTypeUUID, UUID uuid);
}
