package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseVisionAndMissionEntity;

import java.util.UUID;

@Repository
public interface SlaveCourseVisionAndMissionRepository extends ReactiveCrudRepository<SlaveCourseVisionAndMissionEntity, Long> {
    Mono<SlaveCourseVisionAndMissionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCourseVisionAndMissionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCourseVisionAndMissionEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String description, String vision, String mission, String objectives, String outcomes);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndDeletedAtIsNull(String description, String vision, String mission, String objectives, String outcomes);

    Flux<SlaveCourseVisionAndMissionEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String description, Boolean status, String vision, Boolean status2, String mission, Boolean status3, String objectives,Boolean status4,String outcomes,Boolean status5);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String description, Boolean status, String vision, Boolean status2, String mission, Boolean status3, String objectives,Boolean status4,String outcomes,Boolean status5);

}
