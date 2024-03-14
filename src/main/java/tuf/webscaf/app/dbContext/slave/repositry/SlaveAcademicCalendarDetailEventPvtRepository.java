package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEventPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarDetailEventPvtRepository extends ReactiveCrudRepository<SlaveAcademicCalendarDetailEventPvtEntity, Long> {
    Mono<SlaveAcademicCalendarDetailEventPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveAcademicCalendarDetailEventPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
