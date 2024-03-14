package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherAddressRepository extends ReactiveCrudRepository<SlaveTeacherFatherAddressEntity, Long> {


    Flux<SlaveTeacherFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherFatherUUID);

    Flux<SlaveTeacherFatherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherFatherUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String address, UUID teacherFatherUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherFatherUUID, Boolean status);

    Mono<SlaveTeacherFatherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
