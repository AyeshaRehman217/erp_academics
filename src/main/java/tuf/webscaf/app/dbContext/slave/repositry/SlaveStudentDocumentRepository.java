package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentDocumentEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentDocumentRepository extends ReactiveCrudRepository<SlaveStudentDocumentEntity, Long> {
    Mono<SlaveStudentDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentDocumentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentDocumentEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveStudentDocumentEntity> findAllByTitleContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(Pageable pageable, String title, UUID studentUUID, String description, UUID studentUUID1);

    Mono<Long> countByTitleContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(String title, UUID studentUUID, String description, UUID studentUUID1);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String title, String description);

//    Flux<SlaveStudentDocumentEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable,Boolean status);

    Mono<Long> countByDeletedAtIsNull();

//    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveStudentDocumentEntity> findByIdAndDeletedAtIsNull(Long id);
}
