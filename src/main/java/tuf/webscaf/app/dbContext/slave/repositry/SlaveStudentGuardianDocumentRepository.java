package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianDocumentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianDocumentRepository extends ReactiveCrudRepository<SlaveStudentGuardianDocumentEntity, Long> {
    Flux<SlaveStudentGuardianDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveStudentGuardianDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentGuardianDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentGuardianDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentGuardianDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentGuardianUUID, String description, UUID studentGuardianUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(String title, UUID studentGuardianUUID, String description, UUID studentGuardianUUID2);

    //show Student Guardian Documents Against Student UUID, Student Guardian UUID and
    @Query("SELECT std_grd_documents.* from std_grd_documents \n" +
            " join std_guardians\n" +
            " on std_grd_documents.std_guardian_uuid = std_guardians.uuid \n" +
            " join students \n" +
            " on std_guardians.student_uuid = students.uuid \n" +
            " where std_grd_documents.uuid = :studentGuardianDocumentUUID " +
            " and std_guardians.uuid  = :studentGuardianUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_grd_documents.deleted_at is null \n" +
            " and std_guardians.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentGuardianProfileEntity> showDocsAgainstStudentAndStudentGuardian(UUID studentUUID, UUID studentGuardianUUID, UUID studentGuardianDocumentUUID);

}
