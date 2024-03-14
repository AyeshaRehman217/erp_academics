package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTimetableChangeReasonEntity;

import java.util.UUID;

@Repository
public interface SlaveTimetableChangeReasonRepository extends ReactiveCrudRepository<SlaveTimetableChangeReasonEntity, Long> {
    Mono<SlaveTimetableChangeReasonEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveTimetableChangeReasonEntity> findAllByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String timetableChangeReason);

    Mono<Long> countByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNull(String timetableChangeReason);

    /**
     * Fetch Timetable Chanfe
     **/
    Flux<SlaveTimetableChangeReasonEntity> findAllByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNullAndTimetableUUID(Pageable pageable, UUID timetableUUID, String timetableChangeReason);

    Mono<Long> countByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNullAndTimetableUUID(UUID timetableUUID, String timetableChangeReason);

    Mono<SlaveTimetableChangeReasonEntity> findByIdAndDeletedAtIsNull(Long id);
}
