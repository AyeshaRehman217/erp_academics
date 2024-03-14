package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseAddressRepository extends ReactiveCrudRepository<SlaveStudentSpouseAddressEntity, Long> {
    Flux<SlaveStudentSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveStudentSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveStudentSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherSpouseUUID);

    Flux<SlaveStudentSpouseAddressEntity> findAllByAddressContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherSpouseUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(String address, UUID teacherSpouseUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherSpouseUUID, Boolean status);

    Mono<SlaveStudentSpouseAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSpouseAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


}
