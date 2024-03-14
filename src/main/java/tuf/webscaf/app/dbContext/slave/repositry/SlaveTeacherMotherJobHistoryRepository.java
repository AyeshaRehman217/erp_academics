package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherMotherJobHistoryEntity, Long> {
    Flux<SlaveTeacherMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveTeacherMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Flux<SlaveTeacherMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(String occupation, UUID teacherUUID, String designation, UUID teacherUUID2, String organization, UUID teacherUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherUUID, Boolean status, String designation, UUID teacherUUID2, Boolean status2, String organization, UUID teacherUUID3, Boolean status3);

    Mono<SlaveTeacherMotherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherMotherJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Mother Job History Against Teacher UUID, Teacher Mother UUID
    @Query("SELECT teacher_mth_job_history.* from teacher_mth_job_history \n" +
            " join teacher_mothers\n" +
            " on teacher_mth_job_history.teacher_mother_uuid = teacher_mothers.uuid \n" +
            " join teachers \n" +
            " on teacher_mothers.teacher_uuid = teachers.uuid \n" +
            " where teacher_mth_job_history.uuid = :teacherMotherJobHistoryUUID " +
            " and teacher_mothers.uuid  = :teacherMotherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_mth_job_history.deleted_at is null \n" +
            " and teacher_mothers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherMotherJobHistoryEntity> showTeacherMotherJobHistoryAgainstTeacherAndTeacherMother(UUID teacherUUID, UUID teacherMotherUUID, UUID teacherMotherJobHistoryUUID);
}
