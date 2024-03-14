package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherFatherNationalityPvtRepository extends ReactiveCrudRepository<TeacherFatherNationalityPvtEntity, Long> {
    Mono<TeacherFatherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<TeacherFatherNationalityPvtEntity> findAllByTeacherFatherUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherFatherUUID, List<UUID> uuids);

    Flux<TeacherFatherNationalityPvtEntity> findAllByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<TeacherFatherNationalityPvtEntity> findFirstByTeacherFatherUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherFatherUUID, UUID nationalityUUID);

    //Check if Teacher Father is used by Teacher Father Nationalities Pvt
    Mono<TeacherFatherNationalityPvtEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
