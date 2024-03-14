package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineAimEntity;

import java.util.UUID;

@Repository
public interface SubjectOutlineAimRepository extends ReactiveCrudRepository<SubjectOutlineAimEntity, Long> {
    Mono<SubjectOutlineAimEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOutlineAimEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOutlineAimEntity> findFirstByNameIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(String name,UUID subjectOutlineUUID);

    Mono<SubjectOutlineAimEntity> findFirstByNameIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID subjectOutlineUUID, UUID uuid);

    Mono<SubjectOutlineAimEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID);
}
