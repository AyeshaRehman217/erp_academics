package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherAcademicRecordEntity;

import java.util.UUID;

@Repository
public interface TeacherAcademicRecordRepository extends ReactiveCrudRepository<TeacherAcademicRecordEntity, Long> {
    Mono<TeacherAcademicRecordEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherAcademicRecordEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherAcademicRecordEntity> findFirstByDegreeUUIDAndTeacherUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID teacherUUID);

    Mono<TeacherAcademicRecordEntity> findFirstByDegreeUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID teacherUUID, UUID uuid);

    Mono<TeacherAcademicRecordEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<TeacherAcademicRecordEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

//    Mono<TeacherAcademicRecordEntity> findFirstByTeacherDocumentUUIDAndDeletedAtIsNull(UUID teacherDocumentUUID);
}
