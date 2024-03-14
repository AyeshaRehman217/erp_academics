package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSectEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSectionEntity;

import java.util.UUID;

@Repository
public interface SlaveSectionRepository extends ReactiveCrudRepository<SlaveSectionEntity, Long> {
    Mono<SlaveSectionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveSectionEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveSectionEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveSectionEntity> findAllByNameContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull
            (Pageable pageable, String name, UUID courseOfferedUUID, String description, UUID courseOfferedUUID1);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull
            (String name, UUID courseOfferedUUID, String description, UUID courseOfferedUUID1);

    Flux<SlaveSectionEntity> findAllByNameContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNull
            (Pageable pageable, String name, UUID courseOfferedUUID, Boolean status, String description, UUID courseOfferedUUID1, Boolean status1);

    Mono<Long> countByNameContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNull
            (String name, UUID courseOfferedUUID, Boolean status, String description, UUID courseOfferedUUID1, Boolean status1);

    Mono<SlaveSectionEntity> findByIdAndDeletedAtIsNull(Long id);
}
