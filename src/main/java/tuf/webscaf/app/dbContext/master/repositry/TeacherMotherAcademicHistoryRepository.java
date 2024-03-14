package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherAcademicHistoryRepository extends ReactiveCrudRepository<TeacherMotherAcademicHistoryEntity, Long> {
    Mono<TeacherMotherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherMotherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherMotherUUID, UUID uuid);

    Mono<TeacherMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndTeacherMotherUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherMotherUUID);

    Mono<TeacherMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherMotherAcademicHistoryEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUuid);
}
