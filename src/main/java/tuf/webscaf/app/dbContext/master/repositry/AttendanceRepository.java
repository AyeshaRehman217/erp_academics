package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AttendanceEntity;
import tuf.webscaf.app.dbContext.master.entity.CommencementOfClassesEntity;

import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends ReactiveCrudRepository<AttendanceEntity, Long> {
    Mono<AttendanceEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AttendanceEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //check by start time and end time and day and created at (While Storing Record)
    @Query("select attendances.* from attendances" +
            " WHERE attendances.commencement_of_classes_uuid = :commencementOfClassesUUID " +
            " AND attendances.deleted_at is null" +
            " fetch first row only ")
    Mono<AttendanceEntity> findFirstByCommencementOfClasses( UUID commencementOfClassesUUID);


}
