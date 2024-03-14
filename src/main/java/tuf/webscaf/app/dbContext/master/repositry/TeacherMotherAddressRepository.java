package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherAddressRepository extends ReactiveCrudRepository<TeacherMotherAddressEntity, Long> {
    Mono<TeacherMotherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    Mono<TeacherMotherAddressEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherAddressEntity> findFirstByTeacherMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherMotherUUID, UUID addressTypeUUID);

    Mono<TeacherMotherAddressEntity> findFirstByTeacherMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherMotherUUID, UUID addressTypeUUID, UUID uuid);
}
