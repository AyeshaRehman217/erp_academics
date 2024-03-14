package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEntity;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarRepository extends ReactiveCrudRepository<SlaveAcademicCalendarEntity, Long> {

    Mono<SlaveAcademicCalendarEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveAcademicCalendarEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveAcademicCalendarEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveAcademicCalendarEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveAcademicCalendarEntity> findAllByNameContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID academicSessionUUID, String description, UUID academicSessionUUID1);

    Flux<SlaveAcademicCalendarEntity> findAllByNameContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID academicSessionUUID, Boolean status, String description, UUID academicSessionUUID1, Boolean status1);

    Mono<Long> countByNameContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndDeletedAtIsNull(String name, UUID academicSessionUUID, String description, UUID academicSessionUUID1);

    Mono<Long> countByNameContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicSessionUUIDAndStatusAndDeletedAtIsNull(String name, UUID academicSessionUUID, Boolean status, String description, UUID academicSessionUUID1, Boolean status1);

}
