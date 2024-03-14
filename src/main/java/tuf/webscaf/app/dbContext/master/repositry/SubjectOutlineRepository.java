package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineEntity;

import java.util.UUID;

@Repository
public interface SubjectOutlineRepository extends ReactiveCrudRepository<SubjectOutlineEntity, Long> {

    Mono<SubjectOutlineEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOutlineEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOutlineEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SubjectOutlineEntity> findFirstByNameIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(String name,UUID courseSubjectUUID);

    Mono<SubjectOutlineEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<SubjectOutlineEntity> findFirstByNameIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID courseSubjectUUID,UUID uuid);

//    Mono<SubjectOutlineEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
//
//    //Check if Campus Id Exists in Subject Outlines
//    Mono<SubjectOutlineEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);
//
//    Mono<SubjectOutlineEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);
//
//    Mono<SubjectOutlineEntity> findFirstBySubjectOfferedUUIDAndDeletedAtIsNull(UUID subjectUUID);
//
//    Mono<SubjectOutlineEntity> findFirstBySemesterUUIDAndDeletedAtIsNull(UUID semesterUUID);
}
