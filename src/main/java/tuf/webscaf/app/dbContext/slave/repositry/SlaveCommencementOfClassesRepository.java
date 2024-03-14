package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEventPvtEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCommencementOfClassesEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCommencementOfClassesRepository;

import java.util.UUID;

@Repository
public interface SlaveCommencementOfClassesRepository extends ReactiveCrudRepository<SlaveCommencementOfClassesEntity, Long>, SlaveCustomCommencementOfClassesRepository {
    Mono<SlaveCommencementOfClassesEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCommencementOfClassesEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCommencementOfClassesEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String description);

    Flux<SlaveCommencementOfClassesEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String description, Boolean status);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String description);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String description, Boolean status2);


    @Query("select count(*)\n" +
            "from commencement_of_classes \n" +
            "join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            "join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            "join students on commencement_of_classes.student_uuid=students.uuid\n" +
            "where commencement_of_classes.deleted_at is null\n" +
            "and academic_sessions.deleted_at is null\n" +
            "and subjects.deleted_at is null\n" +
            "and students.deleted_at is null \n" +
            " AND (concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) ILIKE concat('%',:key,'%') \n" +
            " or commencement_of_classes.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countAllRecordsWithoutStatus(String key, String description);

    @Query("select count(*) \n" +
            " from commencement_of_classes \n" +
            " join academic_sessions on commencement_of_classes.academic_session_uuid=academic_sessions.uuid\n" +
            " join subjects on commencement_of_classes.subject_uuid=subjects.uuid\n" +
            " join students on commencement_of_classes.student_uuid=students.uuid\n" +
            " where commencement_of_classes.deleted_at is null\n" +
            " and academic_sessions.deleted_at is null\n" +
            " and subjects.deleted_at is null\n" +
            " and students.deleted_at is null \n" +
            " and commencement_of_classes.status = :status " +
            " AND (concat_ws('|',DATE(commencement_of_classes.created_at),academic_sessions.name,subjects.name,students.student_id) ILIKE concat('%',:key,'%') \n" +
            " or commencement_of_classes.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countAllRecordsWithStatus(Boolean status, String key, String description);
}
