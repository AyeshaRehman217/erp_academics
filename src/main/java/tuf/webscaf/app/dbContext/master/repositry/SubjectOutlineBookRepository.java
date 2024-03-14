package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineBookEntity;

import java.util.UUID;

@Repository
public interface SubjectOutlineBookRepository extends ReactiveCrudRepository<SubjectOutlineBookEntity, Long> {
    Mono<SubjectOutlineBookEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectOutlineBookEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectOutlineBookEntity> findFirstByTitleAndAuthorAndEditionAndDeletedAtIsNull(String title, String author, String edition);

    Mono<SubjectOutlineBookEntity> findFirstByTitleAndAuthorAndEditionAndDeletedAtIsNullAndUuidIsNot(String title, String author, String edition, UUID subjectOutlineBookUUID);

    Mono<SubjectOutlineBookEntity> findFirstBySubjectOutlineUUIDAndDeletedAtIsNull(UUID subjectOutlineUUID);
}
