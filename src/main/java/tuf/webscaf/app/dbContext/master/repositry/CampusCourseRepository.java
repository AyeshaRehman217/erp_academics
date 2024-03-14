package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CampusCourseEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampusCourseRepository extends ReactiveCrudRepository<CampusCourseEntity, Long> {
    Mono<CampusCourseEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CampusCourseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<CampusCourseEntity> findAllByCampusUUIDAndCourseUUIDInAndDeletedAtIsNull(UUID campusUUID, List<UUID> courseUUID);

    Flux<CampusCourseEntity> findAllByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Flux<CampusCourseEntity> findByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<CampusCourseEntity> findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(UUID campusUUID, UUID courseUUID);

    Mono<CampusCourseEntity> findByCampusUUIDAndCourseUUIDAndDeletedAtIsNull(UUID campusUUID, UUID courseUUID);

    Mono<CampusCourseEntity> findFirstByCampusUUIDAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(UUID campusUUID, UUID courseUUID,UUID uuid);

//    Mono<CampusCourseEntity> findByCourseUUIDAndSubjectUUIDAndDeletedAtIsNull(UUID courseUuid, UUID subjectUuid);

    Mono<CampusCourseEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);

    Mono<CampusCourseEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);
}
