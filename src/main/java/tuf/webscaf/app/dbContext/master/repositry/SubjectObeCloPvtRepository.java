package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectObeCloPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectObeCloPvtRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectObeCloPvtRepository extends ReactiveCrudRepository<SubjectObeCloPvtEntity, Long> {
    Mono<SubjectObeCloPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectObeCloPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectObeCloPvtEntity> findFirstByCloUUIDAndDeletedAtIsNull(UUID cloUUID);

    Flux<SubjectObeCloPvtEntity> findAllBySubjectObeUUIDAndCloUUIDInAndDeletedAtIsNull(UUID subjectObeUUID, List<UUID> cloUUID);

    Flux<SubjectObeCloPvtEntity> findAllBySubjectObeUUIDAndDeletedAtIsNull(UUID subjectObeUUID);

    Mono<SubjectObeCloPvtEntity> findFirstBySubjectObeUUIDAndCloUUIDAndDeletedAtIsNull(UUID subjectObeUUID, UUID cloUUID);

    Mono<SubjectObeCloPvtEntity> findFirstBySubjectObeUUIDAndDeletedAtIsNull(UUID subjectObeUUID);
}
