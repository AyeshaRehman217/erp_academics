package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherAddressEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherAddressRepository extends ReactiveCrudRepository<TeacherFatherAddressEntity, Long> {
    Mono<TeacherFatherAddressEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherAddressEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherAddressEntity> findFirstByAddressTypeUUIDAndDeletedAtIsNull(UUID addressTypeUuid);

    //Check if Teacher Father Profile is used by Father Address
    Mono<TeacherFatherAddressEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<TeacherFatherAddressEntity> findFirstByTeacherFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(UUID teacherFather,UUID addressTypeUUID);

    Mono<TeacherFatherAddressEntity> findFirstByTeacherFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherFatherUUID,UUID addressTypeUUID,UUID uuid);
}
