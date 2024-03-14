package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildEntity;

import java.util.UUID;

@Repository
public interface TeacherChildRepository extends ReactiveCrudRepository<TeacherChildEntity, Long> {
    Mono<TeacherChildEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherChildEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<TeacherChildEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID studentUUID);

    Mono<TeacherChildEntity> findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID studentUUID, UUID uuid);
}
