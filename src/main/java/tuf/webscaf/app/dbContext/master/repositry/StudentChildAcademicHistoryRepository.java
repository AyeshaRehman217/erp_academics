package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentChildAcademicHistoryRepository extends ReactiveCrudRepository<StudentChildAcademicHistoryEntity, Long> {
    Mono<StudentChildAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentChildUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherChildUUID);

    Mono<StudentChildAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherChildUUID, UUID uuid);

    Mono<StudentChildAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentChildAcademicHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

}
