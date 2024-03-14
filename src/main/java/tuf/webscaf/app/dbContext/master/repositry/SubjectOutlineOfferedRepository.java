package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineOfferedEntity;

import java.util.UUID;

@Repository
public interface SubjectOutlineOfferedRepository extends ReactiveCrudRepository<SubjectOutlineOfferedEntity, Long> {
    Mono<SubjectOutlineOfferedEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOutlineOfferedEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectObeUUIDAndDeletedAtIsNull(UUID subjectOutlineObeUUID);

    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID);

    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectOutlineUUIDAndSubjectOfferedUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID, UUID subjectOfferedUUID);

    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectOutlineUUIDAndSubjectOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(UUID subjectOutlineUUID, UUID subjectOfferedUUID, UUID uuid);

    /**
     * Subject Outline OBE exists or not
     **/
    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectObeUUIDAndSubjectOfferedUUIDAndDeletedAtIsNull(UUID subjectOutlineOBEUUID, UUID subjectOfferedUUID);

    Mono<SubjectOutlineOfferedEntity> findFirstBySubjectObeUUIDAndSubjectOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(UUID subjectOutlineOBEUUID, UUID subjectOfferedUUID, UUID uuid);
}
