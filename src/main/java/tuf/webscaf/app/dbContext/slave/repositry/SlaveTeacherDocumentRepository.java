package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherDocumentRepository extends ReactiveCrudRepository<SlaveTeacherDocumentEntity, Long> {
    Flux<SlaveTeacherDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String description);

    Flux<SlaveTeacherDocumentEntity> findAllByTitleContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID teacherUUID, String description, UUID teacherUUID2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

    Mono<Long> countByTitleContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(String title, UUID teacherUUID, String description, UUID teacherUUID2);

    Mono<SlaveTeacherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
