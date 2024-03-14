package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailHolidayPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarDetailHolidayPvtRepository extends ReactiveCrudRepository<AcademicCalendarDetailHolidayPvtEntity, Long> {
    Mono<AcademicCalendarDetailHolidayPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarDetailHolidayPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarDetailHolidayPvtEntity> findFirstByHolidayUUIDAndDeletedAtIsNull(UUID holidayUUID);

    Mono<AcademicCalendarDetailHolidayPvtEntity> findFirstByAcademicCalendarDetailUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID);

    Flux<AcademicCalendarDetailHolidayPvtEntity> findAllByAcademicCalendarDetailUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID);

    Mono<AcademicCalendarDetailHolidayPvtEntity> findFirstByAcademicCalendarDetailUUIDAndHolidayUUIDAndDeletedAtIsNull(UUID academicCalendarDetailUUID, UUID holidayUUID);

    Flux<AcademicCalendarDetailHolidayPvtEntity> findAllByAcademicCalendarDetailUUIDAndHolidayUUIDInAndDeletedAtIsNull(UUID academicCalendarDetailUUID, List<UUID> ids);
}
