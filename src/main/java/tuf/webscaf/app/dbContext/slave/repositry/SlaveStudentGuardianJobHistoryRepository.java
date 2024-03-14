package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentGuardianJobHistoryEntity, Long> {
    Mono<SlaveStudentGuardianJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Flux<SlaveStudentGuardianJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    Mono<SlaveStudentGuardianJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentGuardianJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Student Guardian Job History Against Student UUID, Student Guardian UUID
    @Query("SELECT std_grd_job_history.* from std_grd_job_history \n" +
            " join std_guardians\n" +
            " on std_grd_job_history.std_guardian_uuid = std_guardians.uuid \n" +
            " join students \n" +
            " on std_guardians.student_uuid = students.uuid \n" +
            " where std_grd_job_history.uuid = :studentGuardianJobHistoryUUID " +
            " and std_guardians.uuid  = :studentGuardianUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_grd_job_history.deleted_at is null \n" +
            " and std_guardians.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentGuardianJobHistoryEntity> showStudentGuardianJobHistoryAgainstStudentAndStudentGuardian(UUID studentUUID, UUID studentGuardianUUID, UUID studentGuardianJobHistoryUUID);
}
