package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTimetableRescheduleEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTimetableCreationRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTimetableRescheduleRepository;

import java.util.UUID;

@Repository
public interface SlaveTimetableRescheduleRepository extends ReactiveCrudRepository<SlaveTimetableRescheduleEntity, Long>, SlaveCustomTimetableRescheduleRepository {
    Flux<SlaveTimetableRescheduleEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveTimetableRescheduleEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Mono<SlaveTimetableRescheduleEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTimetableRescheduleEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Count timetable records with or without status Filter
     **/
    @Query("select count(*) from timetableView\n" +
            " join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid\n" +
            " join subjects on timetableView.subject_uuid=subjects.uuid\n" +
            " where timetableView.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            " and subjects.deleted_at is null " +
            " and timetableView.is_rescheduled is true " +
            " and timetableView.status= :status\n" +
            " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') \n" +
            " AND timetableView.description ILIKE concat('%',:description,'%') ) ")
    Mono<Long> countTimetableRecordWithStatus(Boolean status, String key, String description);

    @Query("select count(*) from timetableView\n" +
            " join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
            " join subjects on timetableView.subject_uuid=subjects.uuid \n" +
            " where timetableView.deleted_at is null \n" +
            " and timetableView.is_rescheduled is true " +
            " and academic_sessions.deleted_at is null \n" +
            " and subjects.deleted_at is null " +
            " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') \n" +
            " AND timetableView.description ILIKE concat('%',:description,'%') ) ")
    Mono<Long> countTimetableRecordWithoutStatus(String key, String description);

    /**
     * Count timetable records with or without status Filter (With Subject Filter)
     **/
    @Query("select count(*) from timetableView\n" +
            " join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid\n" +
            " join subjects on timetableView.subject_uuid=subjects.uuid\n" +
            " where timetableView.deleted_at is null\n" +
            " and timetableView.is_rescheduled is true " +
            " and academic_sessions.deleted_at is null\n" +
            " and subjects.deleted_at is null " +
            " and subjects.uuid =:subjectUUID " +
            " and timetableView.status= :status\n" +
            " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') \n" +
            " AND timetableView.description ILIKE concat('%',:description,'%') ) ")
    Mono<Long> countTimetableRecordWithStatusAgainstSubject(UUID subjectUUID, Boolean status, String key, String description);

    @Query("select count(*) from timetableView\n" +
            " join academic_sessions on timetableView.academic_session_uuid=academic_sessions.uuid \n" +
            " join subjects on timetableView.subject_uuid=subjects.uuid \n" +
            " where timetableView.deleted_at is null \n" +
            " and timetableView.is_rescheduled is true " +
            " and academic_sessions.deleted_at is null \n" +
            " and subjects.deleted_at is null " +
            " and subjects.uuid =:subjectUUID " +
            " AND (concat_ws('|',academic_sessions.name,subjects.name) ILIKE concat('%',:key,'%') \n" +
            " AND timetableView.description ILIKE concat('%',:description,'%') ) ")
    Mono<Long> countTimetableRecordWithoutStatusAgainstSubject(UUID subjectUUID, String key, String description);
}
