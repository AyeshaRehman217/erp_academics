package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherDocumentRepository extends ReactiveCrudRepository<TeacherDocumentEntity, Long> {

    Mono<TeacherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherDocumentEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
