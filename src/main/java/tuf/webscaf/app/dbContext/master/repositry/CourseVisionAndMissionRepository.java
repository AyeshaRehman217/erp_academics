package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseVisionAndMissionEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseVisionAndMissionRepository extends ReactiveCrudRepository<CourseVisionAndMissionEntity, Long> {
    Mono<CourseVisionAndMissionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseVisionAndMissionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<CourseVisionAndMissionEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);

    Mono<CourseVisionAndMissionEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID CourseUUID);

    Mono<CourseVisionAndMissionEntity> findFirstByVisionAndCourseUUIDAndDeletedAtIsNull(String name,UUID CourseUUID);

    Mono<CourseVisionAndMissionEntity> findFirstByMissionAndCourseUUIDAndDeletedAtIsNull(String name,UUID CourseUUID);

    Mono<CourseVisionAndMissionEntity> findFirstByVisionAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID CourseUUID,UUID uuid);

    Mono<CourseVisionAndMissionEntity> findFirstByMissionAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID CourseUUID,UUID uuid);
}
