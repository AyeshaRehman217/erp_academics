package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherAddressRepository extends ReactiveCrudRepository<SlaveStudentFatherAddressEntity, Long> {
    Mono<SlaveStudentFatherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID studentFatherUUID);

    Flux<SlaveStudentFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID studentFatherUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String address, UUID studentFatherUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentFatherUUID, Boolean status);

    Mono<SlaveStudentFatherAddressEntity> findByIdAndDeletedAtIsNull(Long id);
}
