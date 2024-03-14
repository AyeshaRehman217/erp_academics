package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingAcademicHistoryRepository extends ReactiveCrudRepository<TeacherSiblingAcademicHistoryEntity, Long> {
    Mono<TeacherSiblingAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherSiblingUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherSiblingUUID);

    Mono<TeacherSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherSiblingUUID, UUID uuid);

    Mono<TeacherSiblingAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherSiblingAcademicHistoryEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
