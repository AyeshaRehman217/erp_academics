package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingDocumentRepository extends ReactiveCrudRepository<SlaveStudentSiblingDocumentEntity, Long> {
    Flux<SlaveStudentSiblingDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveStudentSiblingDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSiblingDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentSiblingDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentSiblingDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentSiblingUUID, String description, UUID studentSiblingUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String title, UUID studentSiblingUUID, String description, UUID studentSiblingUUID2);

    //show Student Sibling Documents Against Student UUID, Student Sibling UUID and
    @Query("SELECT std_sibling_documents.* from std_sibling_documents \n" +
            " join std_siblings\n" +
            " on std_sibling_documents.std_sibling_uuid = std_siblings.uuid \n" +
            " join students \n" +
            " on std_siblings.student_uuid = students.uuid \n" +
            " where std_sibling_documents.uuid = :studentSiblingDocumentUUID " +
            " and std_siblings.uuid  = :studentSiblingUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_sibling_documents.deleted_at is null \n" +
            " and std_siblings.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSiblingProfileEntity> showDocsAgainstStudentAndStudentSibling(UUID studentUUID, UUID studentSiblingUUID, UUID studentSiblingDocumentUUID);

}
