package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentSpouseJobHistoryEntity, Long> {
    Flux<SlaveStudentSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSpouseUUID, String designation, UUID teacherSpouseUUID2, String organization, UUID teacherSpouseUUID3);

    Flux<SlaveStudentSpouseJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSpouseUUID, Boolean status, String designation, UUID teacherSpouseUUID2, Boolean status2, String organization, UUID teacherSpouseUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(String occupation, UUID teacherSpouseUUID, String designation, UUID teacherSpouseUUID2, String organization, UUID teacherSpouseUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherSpouseUUID, Boolean status, String designation, UUID teacherSpouseUUID2, Boolean status2, String organization, UUID teacherSpouseUUID3, Boolean status3);

    Mono<SlaveStudentSpouseJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSpouseJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentSpouseJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Student Spouse Job History Against Student UUID, Student Spouse UUID
    @Query("SELECT std_spouse_job_history.* from std_spouse_job_history \n" +
            " join std_spouses\n" +
            " on std_spouse_job_history.std_spouse_uuid = std_spouses.uuid \n" +
            " join students \n" +
            " on std_spouses.student_uuid = students.uuid \n" +
            " where std_spouse_job_history.uuid = :studentSpouseJobHistoryUUID " +
            " and std_spouses.uuid  = :studentSpouseUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_spouse_job_history.deleted_at is null \n" +
            " and std_spouses.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSpouseJobHistoryEntity> showStudentSpouseJobHistoryAgainstStudentAndStudentSpouse(UUID studentUUID, UUID studentSpouseUUID, UUID studentSpouseJobHistoryUUID);
}
