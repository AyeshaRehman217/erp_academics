package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseAcademicHistoryRepository extends ReactiveCrudRepository<TeacherSpouseAcademicHistoryEntity, Long> {
    Mono<TeacherSpouseAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherSpouseAcademicHistoryEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherSpouseUUID);

    Mono<TeacherSpouseAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherSpouseUUID, UUID uuid);
}
