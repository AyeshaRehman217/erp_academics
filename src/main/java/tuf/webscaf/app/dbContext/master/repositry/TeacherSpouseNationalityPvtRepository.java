package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSpouseNationalityPvtRepository extends ReactiveCrudRepository<TeacherSpouseNationalityPvtEntity, Long> {
    Mono<TeacherSpouseNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherSpouseNationalityPvtEntity> findAllByTeacherSpouseUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> uuids);

    Flux<TeacherSpouseNationalityPvtEntity> findAllByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseNationalityPvtEntity> findFirstByTeacherSpouseUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID nationalityUUID);

    Mono<TeacherSpouseNationalityPvtEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

}
