package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherMotherNationalityPvtRepository extends ReactiveCrudRepository<TeacherMotherNationalityPvtEntity, Long> {
    Mono<TeacherMotherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherMotherNationalityPvtEntity> findAllByTeacherMotherUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherMotherUUID, List<UUID> uuids);

    Flux<TeacherMotherNationalityPvtEntity> findAllByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherNationalityPvtEntity> findFirstByTeacherMotherUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherMotherUUID, UUID nationalityUUID);

    Mono<TeacherMotherNationalityPvtEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
