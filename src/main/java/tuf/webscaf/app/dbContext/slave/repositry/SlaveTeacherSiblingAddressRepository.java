package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingAddressRepository extends ReactiveCrudRepository<SlaveTeacherSiblingAddressEntity, Long> {
    Flux<SlaveTeacherSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address, Pageable pageable);

    Flux<SlaveTeacherSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status, Pageable pageable);

    Flux<SlaveTeacherSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(String address, UUID teacherSiblingUUID, Pageable pageable);

    Flux<SlaveTeacherSiblingAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherSiblingUUID, Boolean status, Pageable pageable);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(String address, UUID teacherSiblingUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherSiblingUUID, Boolean status);

    Mono<SlaveTeacherSiblingAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSiblingAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
