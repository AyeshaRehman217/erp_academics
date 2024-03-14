package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentDocumentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentDocumentRepository extends ReactiveCrudRepository<StudentDocumentEntity, Long> {

    Mono<StudentDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<StudentDocumentEntity> findByStudentProfileUUIDAndDocumentUUIDAndDeletedAtIsNull(UUID stdUuid, UUID documentUuid);

    Flux<StudentDocumentEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Mono<StudentDocumentEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

}
