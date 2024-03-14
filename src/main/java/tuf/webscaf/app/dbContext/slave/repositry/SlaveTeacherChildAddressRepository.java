package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildAddressRepository extends ReactiveCrudRepository<SlaveTeacherChildAddressEntity, Long> {
    Flux<SlaveTeacherChildAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherChildAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherChildAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherChildUUID);

    Flux<SlaveTeacherChildAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherChildUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(String address, UUID teacherChildUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherChildUUID, Boolean status);

    Mono<SlaveTeacherChildAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherChildAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
