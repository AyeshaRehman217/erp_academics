package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseDocumentRepository extends ReactiveCrudRepository<SlaveStudentSpouseDocumentEntity, Long> {
    Flux<SlaveStudentSpouseDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveStudentSpouseDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSpouseDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentSpouseDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentSpouseDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentSpouseUUID, String description, UUID studentSpouseUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(String title, UUID studentSpouseUUID, String description, UUID studentSpouseUUID2);

    //show Student Spouse Documents Against Student UUID, Student Spouse UUID and
    @Query("SELECT std_spouse_documents.* from std_spouse_documents \n" +
            " join std_spouses\n" +
            " on std_spouse_documents.std_spouse_uuid = std_spouses.uuid \n" +
            " join students \n" +
            " on std_spouses.student_uuid = students.uuid \n" +
            " where std_spouse_documents.uuid = :studentSpouseDocumentUUID " +
            " and std_spouses.uuid  = :studentSpouseUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_spouse_documents.deleted_at is null \n" +
            " and std_spouses.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSpouseProfileEntity> showDocsAgainstStudentAndStudentSpouse(UUID studentUUID, UUID studentSpouseUUID, UUID studentSpouseDocumentUUID);
}
