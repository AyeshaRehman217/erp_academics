package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherGuardianNationalityPvtRepository extends ReactiveCrudRepository<TeacherGuardianNationalityPvtEntity, Long> {
    Mono<TeacherGuardianNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherGuardianNationalityPvtEntity> findAllByTeacherGuardianUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherGuardianUUID, List<UUID> uuids);

    Flux<TeacherGuardianNationalityPvtEntity> findAllByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianNationalityPvtEntity> findFirstByTeacherGuardianUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID nationalityUUID);

    Mono<TeacherGuardianNationalityPvtEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

}
