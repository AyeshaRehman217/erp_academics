package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianDocumentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface StudentGuardianDocumentRepository extends ReactiveCrudRepository<StudentGuardianDocumentEntity, Long> {
    Mono<StudentGuardianDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianDocumentEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianUUID);
}
