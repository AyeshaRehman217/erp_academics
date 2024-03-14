package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherSpouseDocumentRepository extends ReactiveCrudRepository<TeacherSpouseDocumentEntity, Long> {
    Mono<TeacherSpouseDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseDocumentEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
