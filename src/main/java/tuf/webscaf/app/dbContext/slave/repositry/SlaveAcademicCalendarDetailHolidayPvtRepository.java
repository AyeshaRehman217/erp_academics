package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailHolidayPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarDetailHolidayPvtRepository extends ReactiveCrudRepository<SlaveAcademicCalendarDetailHolidayPvtEntity, Long> {
    Mono<SlaveAcademicCalendarDetailHolidayPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveAcademicCalendarDetailHolidayPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
