package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildDocumentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildDocumentRepository extends ReactiveCrudRepository<SlaveStudentChildDocumentEntity, Long> {

    Mono<SlaveStudentChildDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentChildDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveStudentChildDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentChildUUID, String description, UUID studentChildUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(String title, UUID studentChildUUID, String description, UUID studentChildUUID2);

    //show Student Child Documents Against Student UUID, Student Child UUID and
    @Query("SELECT std_child_documents.* from std_child_documents \n" +
            " join std_childs\n" +
            " on std_child_documents.std_child_uuid = std_childs.uuid \n" +
            " join students \n" +
            " on std_childs.student_uuid = students.uuid \n" +
            " where std_child_documents.uuid = :studentChildDocumentUUID " +
            " and std_childs.uuid  = :studentChildUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_child_documents.deleted_at is null \n" +
            " and std_childs.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentChildProfileEntity> showDocsAgainstStudentAndStudentChild(UUID studentUUID, UUID studentChildUUID, UUID studentChildDocumentUUID);
}
