package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarPlanEntity;

import java.util.UUID;

@Repository
public interface AcademicCalendarPlanRepository extends ReactiveCrudRepository<AcademicCalendarPlanEntity, Long> {

    Mono<AcademicCalendarPlanEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AcademicCalendarPlanEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AcademicCalendarPlanEntity> findFirstByNameIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull(String name, UUID academicCalendarUUID);

    Mono<AcademicCalendarPlanEntity> findFirstByNameIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID academicCalendarUUID, UUID uuid);

    Mono<AcademicCalendarPlanEntity> findFirstByAcademicCalendarUUIDAndDeletedAtIsNull(UUID academicCalendarUUID);

}
