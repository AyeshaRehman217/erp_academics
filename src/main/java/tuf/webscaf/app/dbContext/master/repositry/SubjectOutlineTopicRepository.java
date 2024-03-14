package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineTopicEntity;

import java.util.UUID;

@Repository
public interface SubjectOutlineTopicRepository extends ReactiveCrudRepository<SubjectOutlineTopicEntity, Long> {
    Mono<SubjectOutlineTopicEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOutlineTopicEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOutlineTopicEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID);

    Mono<SubjectOutlineTopicEntity> findFirstByLectureTypeUUIDAndDeletedAtIsNull(UUID lectureTypeUUID);
}
