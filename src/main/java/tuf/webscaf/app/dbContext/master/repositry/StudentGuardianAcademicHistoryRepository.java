package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianAcademicHistoryRepository extends ReactiveCrudRepository<StudentGuardianAcademicHistoryEntity, Long> {
    Mono<StudentGuardianAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentGuardianAcademicHistoryEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);

    Mono<StudentGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentGuardianUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID studentGuardianUUID);

    Mono<StudentGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID studentGuardianUUID, UUID uuid);
}
