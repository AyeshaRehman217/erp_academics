package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherDocumentRepository extends ReactiveCrudRepository<SlaveTeacherMotherDocumentEntity, Long> {
    Flux<SlaveTeacherMotherDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveTeacherMotherDocumentEntity> findAllByTitleContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID teacherMotherUUID, String description, UUID teacherMotherUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(String title, UUID teacherMotherUUID, String description, UUID teacherMotherUUID2);

    Mono<SlaveTeacherMotherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //show Teacher Mother Documents Against Teacher UUID, Teacher Mother UUID and
    @Query("SELECT teacher_mth_documents.* from teacher_mth_documents \n" +
            " join teacher_mothers\n" +
            " on teacher_mth_documents.teacher_mother_uuid = teacher_mothers.uuid \n" +
            " join teachers \n" +
            " on teacher_mothers.teacher_uuid = teachers.uuid \n" +
            " where teacher_mth_documents.uuid = :teacherMotherDocumentUUID " +
            " and teacher_mothers.uuid  = :teacherMotherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_mth_documents.deleted_at is null \n" +
            " and teacher_mothers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherMotherProfileEntity> showDocsAgainstTeacherAndTeacherMother(UUID teacherUUID, UUID teacherMotherUUID, UUID teacherMotherDocumentUUID);

}
