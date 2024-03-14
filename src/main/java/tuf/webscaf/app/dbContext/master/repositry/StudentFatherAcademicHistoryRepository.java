package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentFatherAcademicHistoryRepository extends ReactiveCrudRepository<StudentFatherAcademicHistoryEntity, Long> {
    Mono<StudentFatherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentFatherUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID studentFatherUUID);

    Mono<StudentFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentFatherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID studentFatherUUID, UUID uuid);

    Mono<StudentFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentFatherAcademicHistoryEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);
}
