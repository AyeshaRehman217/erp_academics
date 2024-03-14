package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseDocumentEntity;

import java.util.UUID;

@Repository
public interface StudentSpouseDocumentRepository extends ReactiveCrudRepository<StudentSpouseDocumentEntity, Long> {
    Mono<StudentSpouseDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseDocumentEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
