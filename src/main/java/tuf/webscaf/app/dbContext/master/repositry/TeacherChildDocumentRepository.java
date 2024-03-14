package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherChildDocumentRepository extends ReactiveCrudRepository<TeacherChildDocumentEntity, Long> {
    Mono<TeacherChildDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildDocumentEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
