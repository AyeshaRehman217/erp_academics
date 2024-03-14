package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSubjectEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSubjectRepository extends ReactiveCrudRepository<TeacherSubjectEntity, Long> {
    Mono<TeacherSubjectEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSubjectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSubjectEntity> findFirstByTeacherUUIDAndCourseSubjectUUIDAndAcademicSessionUUIDAndDeletedAtIsNull
            (UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID);

    Mono<TeacherSubjectEntity> findFirstByTeacherUUIDAndCourseSubjectUUIDAndAcademicSessionUUIDAndDeletedAtIsNullAndUuidIsNot
            (UUID teacherUUID, UUID courseSubjectUUID, UUID academicSessionUUID, UUID uuid);

}
