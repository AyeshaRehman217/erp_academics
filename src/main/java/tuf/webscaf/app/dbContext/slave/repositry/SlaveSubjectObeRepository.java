package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectObeEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectObeRepository extends ReactiveCrudRepository<SlaveSubjectObeEntity, Long> {
    Mono<SlaveSubjectObeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveSubjectObeEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveSubjectObeEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveSubjectObeEntity> findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID courseSubjectUUID, String description, UUID courseSubjectUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(String name, UUID courseSubjectUUID, String description, UUID courseSubjectUUID2);

    Flux<SlaveSubjectObeEntity> findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID courseSubjectUUID, Boolean status, String description, UUID courseSubjectUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(String name, UUID courseSubjectUUID, Boolean status, String description, UUID courseSubjectUUID2, Boolean status2);
}
