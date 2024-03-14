package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentFatherJobHistoryEntity, Long> {

    Flux<SlaveStudentFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentFatherUUID, String designation, UUID studentFatherUUID2, String organization, UUID studentFatherUUID3);

    Flux<SlaveStudentFatherJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentFatherUUID, Boolean status, String designation, UUID studentFatherUUID2, Boolean status2, String organization, UUID studentFatherUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String occupation, UUID studentFatherUUID, String designation, UUID studentFatherUUID2, String organization, UUID studentFatherUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID studentFatherUUID, Boolean status, String designation, UUID studentFatherUUID2, Boolean status2, String organization, UUID studentFatherUUID3, Boolean status3);

    Mono<SlaveStudentFatherJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentFatherJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Student Father Job History Against Student UUID, Student Father UUID
    @Query("SELECT std_fth_job_history.* from std_fth_job_history \n" +
            " join std_fathers\n" +
            " on std_fth_job_history.std_father_uuid = std_fathers.uuid \n" +
            " join students \n" +
            " on std_fathers.student_uuid = students.uuid \n" +
            " where std_fth_job_history.uuid = :studentFatherJobHistoryUUID " +
            " and std_fathers.uuid  = :studentFatherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_fth_job_history.deleted_at is null \n" +
            " and std_fathers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentFatherJobHistoryEntity> showStudentFatherJobHistoryAgainstStudentAndStudentFather(UUID studentUUID, UUID studentFatherUUID, UUID studentFatherJobHistoryUUID);
}
