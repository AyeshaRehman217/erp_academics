package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectRepository extends ReactiveCrudRepository<SubjectEntity, Long> {
    Mono<SubjectEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SubjectEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Mono<SubjectEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<SubjectEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<SubjectEntity> findFirstByShortNameIgnoreCaseAndDeletedAtIsNull(String shortName);

    Mono<SubjectEntity> findFirstByShortNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String shortName, UUID uuid);

    Mono<SubjectEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNull(String subjectCode);

    Mono<SubjectEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String subjectCode, UUID uuid);

    Mono<SubjectEntity> findFirstBySlugAndDeletedAtIsNull(String slug);

    Mono<SubjectEntity> findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(String slug, UUID uuid);
}
