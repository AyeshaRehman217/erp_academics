package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarEventTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicCalendarEventTypeRepository extends ReactiveCrudRepository<AcademicCalendarEventTypeEntity, Long> {
    Mono<AcademicCalendarEventTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarEventTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarEventTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AcademicCalendarEventTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<AcademicCalendarEventTypeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
