package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseAddressEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseAddressRepository extends ReactiveCrudRepository<StudentSpouseAddressEntity, Long> {
    Mono<StudentSpouseAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<StudentSpouseAddressEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseAddressEntity> findFirstByStudentSpouseUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID addressTypeUUID);

    Mono<StudentSpouseAddressEntity> findFirstByStudentSpouseUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, UUID addressTypeUUID, UUID uuid);
}
