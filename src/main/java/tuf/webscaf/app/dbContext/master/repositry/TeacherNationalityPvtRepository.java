package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherNationalityPvtRepository extends ReactiveCrudRepository<TeacherNationalityPvtEntity, Long> {
    Mono<TeacherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Mono<TeacherNationalityPvtEntity> findFirstByTeacherUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID nationalityUUID);

    Flux<TeacherNationalityPvtEntity> findAllByTeacherUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherUUID, List<UUID> uuids);

    Flux<TeacherNationalityPvtEntity> findAllByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherNationalityPvtEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

}
