package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectOutlineRepository extends ReactiveCrudRepository<SlaveSubjectOutlineEntity, Long> {
    Flux<SlaveSubjectOutlineEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveSubjectOutlineEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveSubjectOutlineEntity> findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID courseSubjectUUID, String description, UUID courseSubjectUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndDeletedAtIsNull(String name, UUID courseSubjectUUID, String description, UUID courseSubjectUUID2);

    Flux<SlaveSubjectOutlineEntity> findAllByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID courseSubjectUUID, Boolean status, String description, UUID courseSubjectUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseSubjectUUIDAndStatusAndDeletedAtIsNull(String name, UUID courseSubjectUUID, Boolean status, String description, UUID courseSubjectUUID2, Boolean status2);


    Mono<SlaveSubjectOutlineEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectOutlineEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
