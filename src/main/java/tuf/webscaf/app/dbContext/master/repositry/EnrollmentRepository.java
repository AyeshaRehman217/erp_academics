package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.EnrollmentEntity;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends ReactiveCrudRepository<EnrollmentEntity, Long> {
    Mono<EnrollmentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<EnrollmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<EnrollmentEntity> findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(UUID academicSessionUUID,UUID uuid);

    Mono<EnrollmentEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<EnrollmentEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndIdIsNot(UUID studentUUID, Long id);

    //check if student is already enrolled in the same subject offered academic session and semester
    Mono<EnrollmentEntity> findFirstByStudentUUIDAndSubjectOfferedUUIDAndAcademicSessionUUIDAndSemesterUUIDAndDeletedAtIsNull(UUID studentUUID, UUID subjectOfferedUUID, UUID academicSessionUUID, UUID semesterUUID);

    Mono<EnrollmentEntity> findFirstByStudentUUIDAndSubjectOfferedUUIDAndAcademicSessionUUIDAndSemesterUUIDAndDeletedAtIsNullAndUuidIsNot(UUID studentUUID, UUID subjectOfferedUUID, UUID academicSessionUUID, UUID semesterUUID, UUID uuid);

    //Check if Semester Id Exists in Enrollments
    Mono<EnrollmentEntity> findFirstBySemesterUUIDAndDeletedAtIsNull(UUID semesterUUID);

    Mono<EnrollmentEntity> findFirstBySubjectOfferedUUIDAndDeletedAtIsNull(UUID subjectOfferedUUID);
}
