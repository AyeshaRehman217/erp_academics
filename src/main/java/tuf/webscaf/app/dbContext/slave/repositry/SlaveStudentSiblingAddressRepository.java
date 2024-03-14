package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingAddressRepository extends ReactiveCrudRepository<SlaveStudentSiblingAddressEntity, Long> {
    
    Flux<SlaveStudentSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address, Pageable pageable);

    Flux<SlaveStudentSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status, Pageable pageable);

    Flux<SlaveStudentSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String address, UUID studentSiblingUUID, Pageable pageable);

    Flux<SlaveStudentSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentSiblingUUID, Boolean status, Pageable pageable);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String address, UUID studentSiblingUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentSiblingUUID, Boolean status);

    Mono<SlaveStudentSiblingAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
