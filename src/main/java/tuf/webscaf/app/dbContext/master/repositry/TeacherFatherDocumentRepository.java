package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherDocumentRepository extends ReactiveCrudRepository<TeacherFatherDocumentEntity, Long> {
    Mono<TeacherFatherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherDocumentEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);
}
