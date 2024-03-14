package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarEventEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarEventRepository extends ReactiveCrudRepository<AcademicCalendarEventEntity, Long> {
    Mono<AcademicCalendarEventEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarEventEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarEventEntity> findFirstByAcademicCalendarEventTypeUUIDAndDeletedAtIsNull(UUID academicCalendarEventTypeUUID);

    Mono<AcademicCalendarEventEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AcademicCalendarEventEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<AcademicCalendarEventEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
