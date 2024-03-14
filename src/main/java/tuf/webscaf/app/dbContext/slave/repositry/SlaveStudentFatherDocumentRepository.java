package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherDocumentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherDocumentRepository extends ReactiveCrudRepository<SlaveStudentFatherDocumentEntity, Long> {
    Mono<SlaveStudentFatherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentFatherDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveStudentFatherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentFatherDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentFatherDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentFatherUUID, String description, UUID studentFatherUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String title, UUID studentFatherUUID, String description, UUID studentFatherUUID2);

    //show Student Father Documents Against Student UUID, Student Father UUID and
    @Query("SELECT std_fth_documents.* from std_fth_documents \n" +
            " join std_fathers\n" +
            " on std_fth_documents.std_father_uuid = std_fathers.uuid \n" +
            " join students \n" +
            " on std_fathers.student_uuid = students.uuid \n" +
            " where std_fth_documents.uuid = :studentFatherDocumentUUID " +
            " and std_fathers.uuid  = :studentFatherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_fth_documents.deleted_at is null \n" +
            " and std_fathers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentFatherProfileEntity> showDocsAgainstStudentAndStudentFather(UUID studentUUID, UUID studentFatherUUID, UUID studentFatherDocumentUUID);
}
