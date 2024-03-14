package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherDocumentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherProfileEntity;

import java.util.UUID;

@Repository
public interface StudentMotherDocumentRepository extends ReactiveCrudRepository<StudentMotherDocumentEntity, Long> {
    Mono<StudentMotherDocumentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherDocumentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherDocumentEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID stdProfileUUID);
}
