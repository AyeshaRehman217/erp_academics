package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherGuardianJobHistoryEntity, Long> {
    Flux<SlaveTeacherGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveTeacherGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Flux<SlaveTeacherGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<SlaveTeacherGuardianJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherGuardianJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Guardian Job History Against Teacher UUID, Teacher Guardian UUID
    @Query("SELECT teacher_grd_job_history.* from teacher_grd_job_history \n" +
            " join teacher_guardians\n" +
            " on teacher_grd_job_history.teacher_guardian_uuid = teacher_guardians.uuid \n" +
            " join teachers \n" +
            " on teacher_guardians.teacher_uuid = teachers.uuid \n" +
            " where teacher_grd_job_history.uuid = :teacherGuardianJobHistoryUUID " +
            " and teacher_guardians.uuid  = :teacherGuardianUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_grd_job_history.deleted_at is null \n" +
            " and teacher_guardians.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherGuardianJobHistoryEntity> showTeacherGuardianJobHistoryAgainstTeacherAndTeacherGuardian(UUID teacherUUID, UUID teacherGuardianUUID, UUID teacherGuardianJobHistoryUUID);
}
