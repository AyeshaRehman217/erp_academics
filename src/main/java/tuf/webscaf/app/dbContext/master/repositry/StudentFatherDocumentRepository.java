package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherDocumentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherProfileEntity;

import java.util.UUID;

@Repository
public interface StudentFatherDocumentRepository extends ReactiveCrudRepository<StudentFatherDocumentEntity, Long> {
    Mono<StudentFatherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherDocumentEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID stdFatherUUID);
}
