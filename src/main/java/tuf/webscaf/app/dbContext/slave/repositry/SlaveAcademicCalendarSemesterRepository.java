package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarSemesterEntity;

import java.util.UUID;

@Repository
public interface SlaveAcademicCalendarSemesterRepository extends ReactiveCrudRepository<SlaveAcademicCalendarSemesterEntity, Long> {
    Flux<SlaveAcademicCalendarSemesterEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveAcademicCalendarSemesterEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveAcademicCalendarSemesterEntity> findByIdAndDeletedAtIsNull(Long id);

}
