package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingDocumentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingProfileEntity;

import java.util.UUID;

@Repository
public interface StudentSiblingDocumentRepository extends ReactiveCrudRepository<StudentSiblingDocumentEntity, Long> {
    Mono<StudentSiblingDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingDocumentEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID stdUUID);


}
