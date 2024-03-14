package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherChildAcademicHistoryRepository extends ReactiveCrudRepository<TeacherChildAcademicHistoryEntity, Long> {
    Mono<TeacherChildAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherChildUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherChildUUID);

    Mono<TeacherChildAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherChildUUID, UUID uuid);

    Mono<TeacherChildAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherChildAcademicHistoryEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
