package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.LectureDeliveryModeEntity;

import java.util.UUID;

@Repository
public interface LectureDeliveryModeRepository extends ReactiveCrudRepository<LectureDeliveryModeEntity, Long> {
    Mono<LectureDeliveryModeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<LectureDeliveryModeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<LectureDeliveryModeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<LectureDeliveryModeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
