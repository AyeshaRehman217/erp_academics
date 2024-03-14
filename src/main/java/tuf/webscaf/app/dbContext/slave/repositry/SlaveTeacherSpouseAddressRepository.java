package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseAddressRepository extends ReactiveCrudRepository<SlaveTeacherSpouseAddressEntity, Long> {
    Flux<SlaveTeacherSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherSpouseUUID);

    Flux<SlaveTeacherSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherSpouseUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(String address, UUID teacherSpouseUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherSpouseUUID, Boolean status);

    Mono<SlaveTeacherSpouseAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


}
