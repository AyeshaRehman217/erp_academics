package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherAddressEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherAddressRepository extends ReactiveCrudRepository<SlaveTeacherMotherAddressEntity, Long> {
    Flux<SlaveTeacherMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String address);

    Flux<SlaveTeacherMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String address, Boolean status);

    Flux<SlaveTeacherMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherMotherUUID);

    Flux<SlaveTeacherMotherAddressEntity> findAllByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String address, UUID teacherMotherUUID, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    Mono<Long> countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String address, Boolean status);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(String address, UUID teacherMotherUUID);

    Mono<Long> countByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(String address, UUID teacherMotherUUID, Boolean status);

    Mono<SlaveTeacherMotherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);


}
