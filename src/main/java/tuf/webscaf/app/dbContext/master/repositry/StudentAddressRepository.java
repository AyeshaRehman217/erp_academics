package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentAddressEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentAddressRepository extends ReactiveCrudRepository<StudentAddressEntity, Long> {
    Mono<StudentAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentAddressEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

    Mono<StudentAddressEntity> findFirstByStudentUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID stdUUID,UUID addressTypeUUID);

    Mono<StudentAddressEntity> findFirstByStudentUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID,UUID addressTypeUUID, UUID uuid);

    Flux<StudentAddressEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Flux<StudentAddressEntity> findAllByAddressTypeUUIDInAndDeletedAtIsNull(List<UUID> uuids);
}
