package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AcademicCalendarDetailRepository extends ReactiveCrudRepository<AcademicCalendarDetailEntity, Long> {

    Mono<AcademicCalendarDetailEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarDetailEntity> findFirstByAcademicCalendarUUIDAndDeletedAtIsNull(UUID academicCalendarUUID);

    Mono<AcademicCalendarDetailEntity> findFirstByAcademicCalendarUUIDAndCalendarDateAndDeletedAtIsNull(UUID academicCalendarUUID, LocalDateTime calendarDate);

    Mono<AcademicCalendarDetailEntity> findFirstByAcademicCalendarUUIDAndCalendarDateAndDeletedAtIsNullAndUuidIsNot(UUID academicCalendarUUID, LocalDateTime calendarDate, UUID uuid);
}
