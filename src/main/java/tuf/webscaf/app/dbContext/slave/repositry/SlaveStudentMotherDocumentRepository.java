package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherDocumentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherDocumentRepository extends ReactiveCrudRepository<SlaveStudentMotherDocumentEntity, Long> {
    Flux<SlaveStudentMotherDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentMotherDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByDeletedAtIsNull(Boolean status);

    Mono<SlaveStudentMotherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentMotherDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentMotherDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentMotherUUID, String description, UUID studentMotherUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(String title, UUID studentMotherUUID, String description, UUID studentMotherUUID2);

    //show Student Mother Documents Against Student UUID, Student Mother UUID and
    @Query("SELECT std_mth_documents.* from std_mth_documents \n" +
            " join std_mothers\n" +
            " on std_mth_documents.std_mother_uuid = std_mothers.uuid \n" +
            " join students \n" +
            " on std_mothers.student_uuid = students.uuid \n" +
            " where std_mth_documents.uuid = :studentMotherDocumentUUID " +
            " and std_mothers.uuid  = :studentMotherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_mth_documents.deleted_at is null \n" +
            " and std_mothers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentMotherProfileEntity> showDocsAgainstStudentAndStudentMother(UUID studentUUID, UUID studentMotherUUID, UUID studentMotherDocumentUUID);
}
