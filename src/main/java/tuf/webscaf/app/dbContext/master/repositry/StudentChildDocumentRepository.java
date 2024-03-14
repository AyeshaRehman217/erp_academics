package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildDocumentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildProfileEntity;

import java.util.UUID;

@Repository
public interface StudentChildDocumentRepository extends ReactiveCrudRepository<StudentChildDocumentEntity, Long> {
    Mono<StudentChildDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildDocumentEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID stdProfileUUID);
}
