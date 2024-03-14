package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarPlanEntity;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarPlanRepository extends ReactiveCrudRepository<SlaveAcademicCalendarPlanEntity, Long> {
    Flux<SlaveAcademicCalendarPlanEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveAcademicCalendarPlanEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveAcademicCalendarPlanEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveAcademicCalendarPlanEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveAcademicCalendarPlanEntity> findAllByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID academicCalendarUUID, String description, UUID academicCalendarUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull(String name, UUID academicCalendarUUID, String description, UUID academicCalendarUUID2);

    Flux<SlaveAcademicCalendarPlanEntity> findAllByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID academicCalendarUUID, Boolean status, String description, UUID academicCalendarUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNull(String name, UUID academicCalendarUUID, Boolean status, String description, UUID academicCalendarUUID2, Boolean status2);

    Mono<SlaveAcademicCalendarPlanEntity> findByIdAndDeletedAtIsNull(Long id);
}
