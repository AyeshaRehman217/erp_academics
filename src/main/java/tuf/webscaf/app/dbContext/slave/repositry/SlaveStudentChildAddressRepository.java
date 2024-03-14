package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildAddressRepository extends ReactiveCrudRepository<SlaveStudentChildAddressEntity, Long> {

    Mono<SlaveStudentChildAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentChildAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<SlaveStudentChildAddressEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Flux<SlaveStudentChildAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentChildAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentChildAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID studentChildUUID);

    Flux<SlaveStudentChildAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID studentChildUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(String address, UUID studentChildUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentChildUUID, Boolean status);
}
