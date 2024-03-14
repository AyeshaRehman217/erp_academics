package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineTopicEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectOutlineTopicRepository extends ReactiveCrudRepository<SlaveSubjectOutlineTopicEntity, Long> {
    /**
     * Fetch and Count All Records and Filter based on name and description only
     **/
    Flux<SlaveSubjectOutlineTopicEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    /**
     * Fetch and Count All Records and Filter based on name and description (Status Filter)
     **/
    Flux<SlaveSubjectOutlineTopicEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status1);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status1);

    /**
     * Fetch and Count All Records and Filter based on name and description (Status and Subject Outline UUID)
     **/
    Flux<SlaveSubjectOutlineTopicEntity> findAllByNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, UUID subjectOutlineUUID, String description, Boolean status1, UUID subjectOutlineUUID1);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(String name, Boolean status, UUID subjectOutlineUUID, String description, Boolean status1, UUID subjectOutlineUUID1);

    /**
     * Fetch and Count All Records and Filter based on name and description (Subject Outline UUID Filter)
     **/
    Flux<SlaveSubjectOutlineTopicEntity> findAllByNameContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID subjectOutlineUUID, String description, UUID subjectOutlineUUID1);

    Mono<Long> countByNameContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(String name, UUID subjectOutlineUUID, String description, UUID subjectOutlineUUID1);

    Mono<SlaveSubjectOutlineTopicEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectOutlineTopicEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
