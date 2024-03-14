package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailEventPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarDetailEventPvtRepository extends ReactiveCrudRepository<AcademicCalendarDetailEventPvtEntity, Long> {
    Mono<AcademicCalendarDetailEventPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarDetailEventPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarDetailEventPvtEntity> findFirstByAcademicCalendarEventUUIDAndDeletedAtIsNull(UUID academicCalendarEventUUID);

    Mono<AcademicCalendarDetailEventPvtEntity> findFirstByAcademicCalendarDetailUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID);

    Flux<AcademicCalendarDetailEventPvtEntity> findAllByAcademicCalendarDetailUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID);

    Mono<AcademicCalendarDetailEventPvtEntity> findFirstByAcademicCalendarDetailUUIDAndAcademicCalendarEventUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID, UUID academicCalendarEventUUID);

    Flux<AcademicCalendarDetailEventPvtEntity> findAllByAcademicCalendarDetailUUIDAndAcademicCalendarEventUUIDInAndDeletedAtIsNull(UUID academicCalendarDetailUUID, List<UUID> uuids);
}
