package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSpouseJobHistoryEntity, Long> {
    Flux<SlaveTeacherSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveTeacherSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSpouseUUID, String designation, UUID teacherSpouseUUID2, String organization, UUID teacherSpouseUUID3);

    Flux<SlaveTeacherSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSpouseUUID, Boolean status, String designation, UUID teacherSpouseUUID2, Boolean status2, String organization, UUID teacherSpouseUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(String occupation, UUID teacherSpouseUUID, String designation, UUID teacherSpouseUUID2, String organization, UUID teacherSpouseUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherSpouseUUID, Boolean status, String designation, UUID teacherSpouseUUID2, Boolean status2, String organization, UUID teacherSpouseUUID3, Boolean status3);

    Mono<SlaveTeacherSpouseJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherSpouseJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Spouse Job History Against Teacher UUID, Teacher Spouse UUID
    @Query("SELECT teacher_spouse_job_history.* from teacher_spouse_job_history \n" +
            " join teacher_spouses\n" +
            " on teacher_spouse_job_history.teacher_spouse_uuid = teacher_spouses.uuid \n" +
            " join teachers \n" +
            " on teacher_spouses.teacher_uuid = teachers.uuid \n" +
            " where teacher_spouse_job_history.uuid = :teacherSpouseJobHistoryUUID " +
            " and teacher_spouses.uuid  = :teacherSpouseUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_spouse_job_history.deleted_at is null \n" +
            " and teacher_spouses.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherSpouseJobHistoryEntity> showTeacherSpouseJobHistoryAgainstTeacherAndTeacherSpouse(UUID teacherUUID, UUID teacherSpouseUUID, UUID teacherSpouseJobHistoryUUID);
}
