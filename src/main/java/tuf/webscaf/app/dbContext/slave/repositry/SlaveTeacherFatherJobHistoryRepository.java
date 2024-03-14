package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherFatherJobHistoryEntity, Long> {
    Flux<SlaveTeacherFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation,String organization);

    Flux<SlaveTeacherFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherFatherUUID, String designation, UUID teacherFatherUUID2, String organization, UUID teacherFatherUUID3);

    Flux<SlaveTeacherFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherFatherUUID, Boolean status, String designation, UUID teacherFatherUUID2, Boolean status2, String organization, UUID teacherFatherUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation,String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation,Boolean status1,String designation,Boolean status2,String organization,Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String occupation, UUID teacherFatherUUID, String designation, UUID teacherFatherUUID2, String organization, UUID teacherFatherUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherFatherUUID, Boolean status, String designation, UUID teacherFatherUUID2, Boolean status2, String organization, UUID teacherFatherUUID3, Boolean status3);

    Mono<SlaveTeacherFatherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherFatherJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Father Job History Against Teacher UUID, Teacher Father UUID
    @Query("SELECT teacher_fth_job_history.* from teacher_fth_job_history \n" +
            " join teacher_fathers\n" +
            " on teacher_fth_job_history.teacher_father_uuid = teacher_fathers.uuid \n" +
            " join teachers \n" +
            " on teacher_fathers.teacher_uuid = teachers.uuid \n" +
            " where teacher_fth_job_history.uuid = :teacherFatherJobHistoryUUID " +
            " and teacher_fathers.uuid  = :teacherFatherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_fth_job_history.deleted_at is null \n" +
            " and teacher_fathers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherFatherJobHistoryEntity> showTeacherFatherJobHistoryAgainstTeacherAndTeacherFather(UUID teacherUUID, UUID teacherFatherUUID, UUID teacherFatherJobHistoryUUID);
}
