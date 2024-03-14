package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherChildNationalityPvtRepository extends ReactiveCrudRepository<TeacherChildNationalityPvtEntity, Long> {
    Mono<TeacherChildNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherChildNationalityPvtEntity> findAllByTeacherChildUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> uuids);

    Flux<TeacherChildNationalityPvtEntity> findAllByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildNationalityPvtEntity> findFirstByTeacherChildUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID nationalityUUID);

    Mono<TeacherChildNationalityPvtEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

}
