package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingNationalityPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface TeacherSiblingNationalityPvtRepository extends ReactiveCrudRepository<TeacherSiblingNationalityPvtEntity, Long> {
    Mono<TeacherSiblingNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherSiblingNationalityPvtEntity> findAllByTeacherSiblingUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherSiblingUUID, List<UUID> uuids);

    Flux<TeacherSiblingNationalityPvtEntity> findAllByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingNationalityPvtEntity> findFirstByTeacherSiblingUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID, UUID nationalityUUID);

    Mono<TeacherSiblingNationalityPvtEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
