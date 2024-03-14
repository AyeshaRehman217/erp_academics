package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherOutlineEntity;

import java.util.UUID;

@Repository
public interface TeacherOutlineRepository extends ReactiveCrudRepository<TeacherOutlineEntity, Long> {

    Mono<TeacherOutlineEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherOutlineEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<TeacherOutlineEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
//
//    Mono<TeacherOutlineEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID teacherUUID);
//
//    //Check if Campus Id Exists in Teacher Outlines
//    Mono<TeacherOutlineEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);
//
//    Mono<TeacherOutlineEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);
//
//    Mono<TeacherOutlineEntity> findFirstBySemesterUUIDAndDeletedAtIsNull(UUID semesterUUID);
//
//    Mono<TeacherOutlineEntity> findFirstBySubjectUUIDAndDeletedAtIsNull(UUID subjectUUID);
}
