package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherDocumentRepository extends ReactiveCrudRepository<TeacherMotherDocumentEntity, Long> {

    Mono<TeacherMotherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherDocumentEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
