package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseOfferedEntity;

import java.util.UUID;

@Repository
public interface CourseOfferedRepository extends ReactiveCrudRepository<CourseOfferedEntity, Long> {

    Mono<CourseOfferedEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseOfferedEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CourseOfferedEntity> findFirstByAcademicSessionUUIDAndDeletedAtIsNull(UUID academicSessionUUID);

    Mono<CourseOfferedEntity> findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNull(UUID academicSessionUuid, UUID campusCourseUuid);

    Mono<CourseOfferedEntity> findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID academicSessionUuid, UUID campusCourseUuid,UUID uuid);

}
