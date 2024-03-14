package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianDocumentEntity;

import java.util.UUID;

@Repository
public interface TeacherGuardianDocumentRepository extends ReactiveCrudRepository<TeacherGuardianDocumentEntity, Long> {
    Mono<TeacherGuardianDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianDocumentEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
