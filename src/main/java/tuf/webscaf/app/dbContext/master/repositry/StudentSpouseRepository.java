package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseRepository extends ReactiveCrudRepository<StudentSpouseEntity, Long> {
    Mono<StudentSpouseEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentSpouseEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID studentSpouseUUID);

    Mono<StudentSpouseEntity> findByUuidAndStudentUUIDAndDeletedAtIsNull(UUID uuid, UUID studentUUID);

    Mono<StudentSpouseEntity> findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(UUID studentUUID, UUID studentSpouseUUID);

    Mono<StudentSpouseEntity> findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID studentSpouseUUID, UUID uuid);

    Mono<StudentSpouseEntity> findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNull(UUID studentUUID, UUID teacherUUID);

    Mono<StudentSpouseEntity> findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID teacherUUID, UUID uuid);
}
