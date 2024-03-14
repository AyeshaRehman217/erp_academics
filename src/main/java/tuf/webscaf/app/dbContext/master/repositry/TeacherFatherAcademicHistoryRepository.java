package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherAcademicHistoryRepository extends ReactiveCrudRepository<TeacherFatherAcademicHistoryEntity, Long> {
    Mono<TeacherFatherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherFatherUUID, UUID uuid);

    Mono<TeacherFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherFatherUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherFatherUUID);

    Mono<TeacherFatherAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    //Check if Teacher Father is used by Father Academic History
    Mono<TeacherFatherAcademicHistoryEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
