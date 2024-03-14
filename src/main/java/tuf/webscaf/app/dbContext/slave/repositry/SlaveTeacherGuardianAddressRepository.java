package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianAddressRepository extends ReactiveCrudRepository<SlaveTeacherGuardianAddressEntity, Long> {
    Flux<SlaveTeacherGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherGuardianUUID);

    Flux<SlaveTeacherGuardianAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherGuardianUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String address, UUID teacherGuardianUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherGuardianUUID, Boolean status);

    Mono<SlaveTeacherGuardianAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


}
