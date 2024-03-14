package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends ReactiveCrudRepository<CourseEntity, Long> {

    Mono<CourseEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<CourseEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Mono<CourseEntity> findFirstByNameIgnoreCaseAndCourseLevelUUIDAndDeletedAtIsNull(String name, UUID courseUUID);

    Mono<CourseEntity> findFirstByNameIgnoreCaseAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID courseUUID, UUID uuid);

    Mono<CourseEntity> findFirstByDepartmentUUIDAndDeletedAtIsNull(UUID departmentUUID);

    Mono<CourseEntity> findFirstByCourseLevelUUIDAndDeletedAtIsNull(UUID courseLevelUUID);

    Mono<CourseEntity> findFirstBySlugAndCourseLevelUUIDAndDeletedAtIsNull(String slug, UUID courseLevel);

    Mono<CourseEntity> findFirstBySlugAndCourseLevelUUIDAndDeletedAtIsNullAndUuidIsNot(String slug, UUID courseLevel, UUID uuid);

}
