package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseAddressRepository extends ReactiveCrudRepository<TeacherSpouseAddressEntity, Long> {
    Mono<TeacherSpouseAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherSpouseAddressEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseAddressEntity> findFirstByTeacherSpouseUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID addressTypeUUID);

    Mono<TeacherSpouseAddressEntity> findFirstByTeacherSpouseUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherSpouseUUID, UUID addressTypeUUID, UUID uuid);
}
