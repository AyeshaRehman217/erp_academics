package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherChildJobHistoryEntity, Long> {
    Flux<SlaveTeacherChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveTeacherChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Flux<SlaveTeacherChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<SlaveTeacherChildJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherChildJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherChildJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Child Job History Against Teacher UUID, Teacher Child UUID and
    @Query("SELECT teacher_child_job_history.* from teacher_child_job_history \n" +
            " join teacher_childs\n" +
            " on teacher_child_job_history.teacher_child_uuid = teacher_childs.uuid \n" +
            " join teachers \n" +
            " on teacher_childs.teacher_uuid = teachers.uuid \n" +
            " where teacher_child_job_history.uuid = :teacherChildJobHistoryUUID " +
            " and teacher_childs.uuid  = :teacherChildUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_child_job_history.deleted_at is null \n" +
            " and teacher_childs.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherChildJobHistoryEntity> showTeacherChildJobHistoryAgainstTeacherAndTeacherChild(UUID teacherUUID, UUID teacherChildUUID, UUID teacherChildJobHistoryUUID);
}
