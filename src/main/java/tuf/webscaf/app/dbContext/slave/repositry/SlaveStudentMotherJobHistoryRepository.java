package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentMotherJobHistoryEntity, Long> {
    Flux<SlaveStudentMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Flux<SlaveStudentMotherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    Mono<SlaveStudentMotherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentMotherJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Student Mother Job History Against Student UUID, Student Mother UUID
    @Query("SELECT std_mth_job_history.* from std_mth_job_history \n" +
            " join std_mothers\n" +
            " on std_mth_job_history.std_mother_uuid = std_mothers.uuid \n" +
            " join students \n" +
            " on std_mothers.student_uuid = students.uuid \n" +
            " where std_mth_job_history.uuid = :studentMotherJobHistoryUUID " +
            " and std_mothers.uuid  = :studentMotherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_mth_job_history.deleted_at is null \n" +
            " and std_mothers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentMotherJobHistoryEntity> showStudentMotherJobHistoryAgainstStudentAndStudentMother(UUID studentUUID, UUID studentMotherUUID, UUID studentMotherJobHistoryUUID);
}
