package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentAcademicRecordEntity;

import java.util.UUID;

@Repository
public interface StudentAcademicRecordRepository extends ReactiveCrudRepository<StudentAcademicRecordEntity, Long> {
    Mono<StudentAcademicRecordEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentAcademicRecordEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<StudentAcademicRecordEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

    Mono<StudentAcademicRecordEntity> findFirstByDegreeUUIDAndStudentUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID studentUUID);

//    Mono<StudentAcademicRecordEntity> findFirstByDegreeUUIDAndStudentUUIDAndDeletedAtIsNullAndIdIsNot(UUID degreeUUID, UUID studentUUID);

    Mono<StudentAcademicRecordEntity> findFirstByDegreeUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID studentUUID, UUID uuid);

    Mono<StudentAcademicRecordEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    //Check if Student  Exists in Student Academic Records
    Mono<StudentAcademicRecordEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

//    Mono<StudentAcademicRecordEntity> findFirstByStudentDocumentUUIDAndDeletedAtIsNull(UUID studentDocumentUUID);

}
