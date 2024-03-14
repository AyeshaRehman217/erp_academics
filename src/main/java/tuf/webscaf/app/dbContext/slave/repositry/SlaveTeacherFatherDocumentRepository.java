package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherDocumentRepository extends ReactiveCrudRepository<SlaveTeacherFatherDocumentEntity, Long> {
    Flux<SlaveTeacherFatherDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);


    Flux<SlaveTeacherFatherDocumentEntity> findAllByTitleContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String title, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2);

    Mono<SlaveTeacherFatherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //show Teacher Father Documents Against Teacher UUID, Teacher Father UUID and
    @Query("SELECT teacher_fth_documents.* from teacher_fth_documents \n" +
            " join teacher_fathers\n" +
            " on teacher_fth_documents.teacher_father_uuid = teacher_fathers.uuid \n" +
            " join teachers \n" +
            " on teacher_fathers.teacher_uuid = teachers.uuid \n" +
            " where teacher_fth_documents.uuid = :teacherFatherDocumentUUID " +
            " and teacher_fathers.uuid  = :teacherFatherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_fth_documents.deleted_at is null \n" +
            " and teacher_fathers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherFatherProfileEntity> showDocsAgainstTeacherAndTeacherFather(UUID teacherUUID, UUID teacherFatherUUID, UUID teacherFatherDocumentUUID);

}
