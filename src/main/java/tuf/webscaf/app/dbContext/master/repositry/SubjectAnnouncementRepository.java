package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.SubjectAnnouncementEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectAnnouncementRepository extends ReactiveCrudRepository<SubjectAnnouncementEntity, Long> {
    Mono<SubjectAnnouncementEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SubjectAnnouncementEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SubjectAnnouncementEntity> findFirstByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    Mono<SubjectAnnouncementEntity> findFirstByTitleIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String title, UUID uuid);

    Flux<SubjectAnnouncementEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
