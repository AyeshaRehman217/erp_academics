package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseAcademicHistoryRepository extends ReactiveCrudRepository<StudentSpouseAcademicHistoryEntity, Long> {
    Mono<StudentSpouseAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentSpouseAcademicHistoryEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherSpouseUUID);

    Mono<StudentSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherSpouseUUID, UUID uuid);
}
