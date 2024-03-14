package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseRepository extends ReactiveCrudRepository<TeacherSpouseEntity, Long> {
    Mono<TeacherSpouseEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherSpouseEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<TeacherSpouseEntity> findByUuidAndTeacherUUIDAndDeletedAtIsNull(UUID uuid, UUID teacherUUID);

    Mono<TeacherSpouseEntity> findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID teacherSpouseUUID);

    Mono<TeacherSpouseEntity> findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID teacherSpouseUUID, UUID uuid);

    Mono<TeacherSpouseEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID studentUUID);

    Mono<TeacherSpouseEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID studentUUID, UUID uuid);
}
