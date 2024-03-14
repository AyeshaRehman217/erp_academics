package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherAddressRepository extends ReactiveCrudRepository<SlaveStudentMotherAddressEntity, Long> {
    Flux<SlaveStudentMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID studentMotherUUID);

    Flux<SlaveStudentMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID studentMotherUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(String address, UUID studentMotherUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentMotherUUID, Boolean status);

    Mono<SlaveStudentMotherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
