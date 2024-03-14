package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianRepository extends ReactiveCrudRepository<TeacherGuardianEntity, Long> {
    Mono<TeacherGuardianEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherGuardianEntity> findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID uuid);

    Mono<TeacherGuardianEntity> findFirstByGuardianUUIDAndDeletedAtIsNull(UUID guardianUUID);

    Mono<TeacherGuardianEntity> findFirstByGuardianTypeUUIDAndDeletedAtIsNull(UUID guardianTypeUUID);
}
