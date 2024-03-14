package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentSiblingJobHistoryEntity, Long> {
    Flux<SlaveStudentSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentSiblingUUID, String designation, UUID studentSiblingUUID2, String organization, UUID studentSiblingUUID3);

    Flux<SlaveStudentSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentSiblingUUID, Boolean status, String designation, UUID studentSiblingUUID2, Boolean status2, String organization, UUID studentSiblingUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String occupation, UUID studentSiblingUUID, String designation, UUID studentSiblingUUID2, String organization, UUID studentSiblingUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID studentSiblingUUID, Boolean status, String designation, UUID studentSiblingUUID2, Boolean status2, String organization, UUID studentSiblingUUID3, Boolean status3);

    Mono<SlaveStudentSiblingJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSiblingJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentSiblingJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Student Sibling Job History Against Student UUID, Student Sibling UUID
    @Query("SELECT std_sibling_job_history.* from std_sibling_job_history \n" +
            " join std_siblings\n" +
            " on std_sibling_job_history.std_sibling_uuid = std_siblings.uuid \n" +
            " join students \n" +
            " on std_siblings.student_uuid = students.uuid \n" +
            " where std_sibling_job_history.uuid = :studentSiblingJobHistoryUUID " +
            " and std_siblings.uuid  = :studentSiblingUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_sibling_job_history.deleted_at is null \n" +
            " and std_siblings.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSiblingJobHistoryEntity> showStudentSiblingJobHistoryAgainstStudentAndStudentSibling(UUID studentUUID, UUID studentSiblingUUID, UUID studentSiblingJobHistoryUUID);
}
