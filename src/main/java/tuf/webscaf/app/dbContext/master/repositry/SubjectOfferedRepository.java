package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOfferedEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectOfferedRepository extends ReactiveCrudRepository<SubjectOfferedEntity, Long> {
    Mono<SubjectOfferedEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOfferedEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOfferedEntity> findFirstByAcademicSessionUUIDAndCourseSubjectUUIDAndDeletedAtIsNull(UUID academicSessionUuid, UUID courseSubjectUuid);

    Mono<SubjectOfferedEntity> findFirstByAcademicSessionUUIDAndCourseSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(UUID academicSessionUuid, UUID courseSubjectUuid,UUID uuid);

    Mono<SubjectOfferedEntity> findFirstByAcademicSessionUUIDAndDeletedAtIsNull(UUID academicSessionUUID);

    //check if subject offered exist against academic Session in enrollments
    Mono<SubjectOfferedEntity> findByAcademicSessionUUIDAndUuidAndDeletedAtIsNull(UUID academicSessionUUID,UUID uuid);

    Flux<SubjectOfferedEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Flux<SubjectOfferedEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countAllByDeletedAtIsNull();

//    Flux<SubjectOfferedEntity> findAllByAcademicSessionUUIDAndCourseOfferedUUIDAndSubjectUUIDInAndDeletedAtIsNull(UUID academicSessionUUID, UUID courseUUID,List<UUID> subjectUUID);
//
//    Flux<SubjectOfferedEntity> findAllByAcademicSessionUUIDAndCourseOfferedUUIDAndDeletedAtIsNull(UUID academicSessionUUID, UUID courseUUID);
//
//    Mono<SubjectOfferedEntity> findFirstByAcademicSessionUUIDAndCourseOfferedUUIDAndSubjectUUIDAndDeletedAtIsNull(UUID academicSessionUUID, UUID courseUUID,UUID subjectUUID);
//
//    Mono<SubjectOfferedEntity> findFirstByCourseOfferedUUIDAndDeletedAtIsNull(UUID courseOfferedUUID);
}
