package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentAddressRepository extends ReactiveCrudRepository<SlaveStudentAddressEntity, Long> {
    Flux<SlaveStudentAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID studentUUID);

    Flux<SlaveStudentAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID studentUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(String address, UUID studentUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentUUID, Boolean status);

    Mono<SlaveStudentAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
