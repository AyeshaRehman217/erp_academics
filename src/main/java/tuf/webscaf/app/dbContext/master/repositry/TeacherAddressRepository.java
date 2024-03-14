package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherAddressRepository extends ReactiveCrudRepository<TeacherAddressEntity, Long> {
    Mono<TeacherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherAddressEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherAddressEntity> findFirstByTeacherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID addressTypeUUID);

    Mono<TeacherAddressEntity> findFirstByTeacherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID addressTypeUUID, UUID uuid);
}
