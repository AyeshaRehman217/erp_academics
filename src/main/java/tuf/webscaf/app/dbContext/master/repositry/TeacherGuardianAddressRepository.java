package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianAddressRepository extends ReactiveCrudRepository<TeacherGuardianAddressEntity, Long> {
    Mono<TeacherGuardianAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherGuardianAddressEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianAddressEntity> findFirstByTeacherGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID addressTypeUUID);

    Mono<TeacherGuardianAddressEntity> findFirstByTeacherGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherGuardianUUID, UUID addressTypeUUID, UUID uuid);
}
