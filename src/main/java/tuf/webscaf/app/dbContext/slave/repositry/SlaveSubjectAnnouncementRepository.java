package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectAnnouncementEntity;

import java.util.UUID;

@Repository
public interface SlaveSubjectAnnouncementRepository extends ReactiveCrudRepository<SlaveSubjectAnnouncementEntity, Long> {
    Flux<SlaveSubjectAnnouncementEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveSubjectAnnouncementEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveSubjectAnnouncementEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveSubjectAnnouncementEntity> findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String title, String message);

    Flux<SlaveSubjectAnnouncementEntity> findAllByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String title, Boolean status, String message, Boolean status2);

    Mono<Long> countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndDeletedAtIsNull(String title, String message);

    Mono<Long> countByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String title, Boolean status, String message, Boolean status2);

}
