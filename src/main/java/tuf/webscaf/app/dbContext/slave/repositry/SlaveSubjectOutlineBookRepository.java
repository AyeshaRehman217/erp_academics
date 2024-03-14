package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineBookEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectOutlineBookRepository extends ReactiveCrudRepository<SlaveSubjectOutlineBookEntity, Long> {
    /**
     * Fetch and Count All Records and Filter based on Title and description only
     **/
    Flux<SlaveSubjectOutlineBookEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description, String edition, String isbn, String publisherName, String author, String url);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndDeletedAtIsNull(String title, String description, String edition, String isbn, String publisherName, String author, String url);

    /**
     * Fetch and Count All Records and Filter based on Title and description (Status Filter)
     **/
    Flux<SlaveSubjectOutlineBookEntity> findAllByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String title, Boolean status, String description, Boolean status1, String edition, Boolean status2, String isbn, Boolean status3, String publisherName, Boolean status4, String author, Boolean status5, String url, Boolean status6);

    Mono<Long> countByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String title, Boolean status, String description, Boolean status1, String edition, Boolean status2, String isbn, Boolean status3, String publisherName, Boolean status4, String author, Boolean status5, String url, Boolean status6);

    /**
     * Fetch and Count All Records and Filter based on Title and description (Status and Subject Outline UUID)
     **/
    Flux<SlaveSubjectOutlineBookEntity> findAllByTitleContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(Pageable pageable, String title, Boolean status, UUID subjectOutlineUUID, String description, Boolean status1, UUID subjectOutlineUUID1, String edition, Boolean status2, UUID subjectOutlineUUID2, String isbn, Boolean status3, UUID subjectOutlineUUID3, String publisherName, Boolean status4, UUID subjectOutlineUUID4, String author, Boolean status5, UUID subjectOutlineUUID5, String url, Boolean status6, UUID subjectOutlineUUID6);

    Mono<Long> countByTitleContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(String title, Boolean status, UUID subjectOutlineUUID, String description, Boolean status1, UUID subjectOutlineUUID1, String edition, Boolean status2, UUID subjectOutlineUUID2, String isbn, Boolean status3, UUID subjectOutlineUUID3, String publisherName, Boolean status4, UUID subjectOutlineUUID4, String author, Boolean status5, UUID subjectOutlineUUID5, String url, Boolean status6, UUID subjectOutlineUUID6);

    /**
     * Fetch and Count All Records and Filter based on Title and description (Subject Outline UUID Filter)
     **/
    Flux<SlaveSubjectOutlineBookEntity> findAllByTitleContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID subjectOutlineUUID, String description, UUID subjectOutlineUUID1, String edition, UUID subjectOutlineUUID2, String isbn, UUID subjectOutlineUUID3, String publisherName, UUID subjectOutlineUUID4, String author, UUID subjectOutlineUUID5, String url, UUID subjectOutlineUUID6);

    Mono<Long> countByTitleContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(String title, UUID subjectOutlineUUID, String description, UUID subjectOutlineUUID1, String edition, UUID subjectOutlineUUID2, String isbn, UUID subjectOutlineUUID3, String publisherName, UUID subjectOutlineUUID4, String author, UUID subjectOutlineUUID5, String url, UUID subjectOutlineUUID6);

    Mono<SlaveSubjectOutlineBookEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectOutlineBookEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
