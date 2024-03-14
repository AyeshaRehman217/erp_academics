package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingAddressRepository extends ReactiveCrudRepository<TeacherSiblingAddressEntity, Long> {
    Mono<TeacherSiblingAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherSiblingAddressEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingAddressEntity> findFirstByTeacherSiblingUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID, UUID addressTypeUUID);

    Mono<TeacherSiblingAddressEntity> findFirstByTeacherSiblingUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSiblingUUID,UUID addressTypeUUID,UUID uuid);
}
