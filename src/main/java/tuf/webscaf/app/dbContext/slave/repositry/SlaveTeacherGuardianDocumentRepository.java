package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianDocumentRepository extends ReactiveCrudRepository<SlaveTeacherGuardianDocumentEntity, Long> {
    Flux<SlaveTeacherGuardianDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveTeacherGuardianDocumentEntity> findAllByTitleContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID teacherGuardianUUID, String description, UUID teacherGuardianUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String title, UUID teacherGuardianUUID, String description, UUID teacherGuardianUUID2);

    Mono<SlaveTeacherGuardianDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
    
    //show Teacher Guardian Documents Against Teacher UUID, Teacher Guardian UUID and
    @Query("SELECT teacher_grd_documents.* from teacher_grd_documents \n" +
            " join teacher_guardians\n" +
            " on teacher_grd_documents.teacher_guardian_uuid = teacher_guardians.uuid \n" +
            " join teachers \n" +
            " on teacher_guardians.teacher_uuid = teachers.uuid \n" +
            " where teacher_grd_documents.uuid = :teacherGuardianDocumentUUID " +
            " and teacher_guardians.uuid  = :teacherGuardianUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_grd_documents.deleted_at is null \n" +
            " and teacher_guardians.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherGuardianProfileEntity> showDocsAgainstTeacherAndTeacherGuardian(UUID teacherUUID, UUID teacherGuardianUUID, UUID teacherGuardianDocumentUUID);


}
