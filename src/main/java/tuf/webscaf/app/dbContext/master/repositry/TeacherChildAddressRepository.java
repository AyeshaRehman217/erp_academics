package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherChildAddressRepository extends ReactiveCrudRepository<TeacherChildAddressEntity, Long> {
    Mono<TeacherChildAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherChildAddressEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildAddressEntity> findFirstByTeacherChildUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID addressTypeUUID);

    Mono<TeacherChildAddressEntity> findFirstByTeacherChildUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherChildUUID,UUID addressTypeUUID, UUID uuid);
}
