package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianAddressRepository extends ReactiveCrudRepository<SlaveStudentGuardianAddressEntity, Long> {

    Mono<SlaveStudentGuardianAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentGuardianAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID studentGuardianUUID);

    Flux<SlaveStudentGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID studentGuardianUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(String address, UUID studentGuardianUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(String address, UUID studentGuardianUUID, Boolean status);

}
