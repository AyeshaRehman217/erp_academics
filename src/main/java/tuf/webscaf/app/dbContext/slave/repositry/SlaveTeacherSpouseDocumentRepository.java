package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseDocumentRepository extends ReactiveCrudRepository<SlaveTeacherSpouseDocumentEntity, Long> {
    Flux<SlaveTeacherSpouseDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveTeacherSpouseDocumentEntity> findAllByTitleContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID teacherSpouseUUID, String description, UUID teacherSpouseUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(String title, UUID teacherSpouseUUID, String description, UUID teacherSpouseUUID2);

    Mono<SlaveTeacherSpouseDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //show Teacher Spouse Documents Against Teacher UUID, Teacher Spouse UUID and
    @Query("SELECT teacher_spouse_documents.* from teacher_spouse_documents \n" +
            " join teacher_spouses\n" +
            " on teacher_spouse_documents.teacher_spouse_uuid = teacher_spouses.uuid \n" +
            " join teachers \n" +
            " on teacher_spouses.teacher_uuid = teachers.uuid \n" +
            " where teacher_spouse_documents.uuid = :teacherSpouseDocumentUUID " +
            " and teacher_spouses.uuid  = :teacherSpouseUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_spouse_documents.deleted_at is null \n" +
            " and teacher_spouses.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherSpouseProfileEntity> showDocsAgainstTeacherAndTeacherSpouse(UUID teacherUUID, UUID teacherSpouseUUID, UUID teacherSpouseDocumentUUID);

}
