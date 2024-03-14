package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingRepository extends ReactiveCrudRepository<TeacherSiblingEntity, Long> {
    Mono<TeacherSiblingEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherSiblingEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<TeacherSiblingEntity> findByUuidAndTeacherUUIDAndDeletedAtIsNull(UUID uuid, UUID teacherUUID);

    Mono<TeacherSiblingEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID studentUUID);

    Mono<TeacherSiblingEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID studentUUID, UUID uuid);
}
