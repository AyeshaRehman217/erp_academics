package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherAddressRepository extends ReactiveCrudRepository<SlaveTeacherAddressEntity, Long> {
    Flux<SlaveTeacherAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherUUID);

    Flux<SlaveTeacherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(String address, UUID teacherUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherUUID, Boolean status);

    Mono<SlaveTeacherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
