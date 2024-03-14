package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingAcademicHistoryRepository extends ReactiveCrudRepository<StudentSiblingAcademicHistoryEntity, Long> {
    Mono<StudentSiblingAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentSiblingAcademicHistoryEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);

    Mono<StudentSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentSiblingUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID studentSiblingUUID);

    Mono<StudentSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID studentSiblingUUID, UUID uuid);
}
