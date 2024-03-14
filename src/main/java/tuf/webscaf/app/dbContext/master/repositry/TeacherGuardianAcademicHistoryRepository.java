package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianAcademicHistoryRepository extends ReactiveCrudRepository<TeacherGuardianAcademicHistoryEntity, Long> {
    Mono<TeacherGuardianAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherGuardianUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherGuardianUUID);

    Mono<TeacherGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherGuardianUUID, UUID uuid);

    Mono<TeacherGuardianAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherGuardianAcademicHistoryEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
