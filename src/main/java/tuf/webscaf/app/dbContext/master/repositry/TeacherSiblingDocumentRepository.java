package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherSiblingDocumentRepository extends ReactiveCrudRepository<TeacherSiblingDocumentEntity, Long> {
    Mono<TeacherSiblingDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingDocumentEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
