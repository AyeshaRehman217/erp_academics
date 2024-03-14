package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildJobHistoryRepository extends ReactiveCrudRepository<SlaveStudentChildJobHistoryEntity, Long> {

    Mono<SlaveStudentChildJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentChildJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    Mono<SlaveStudentChildJobHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Flux<SlaveStudentChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveStudentChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveStudentChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Flux<SlaveStudentChildJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status1, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(String occupation, UUID studentUUID, String designation, UUID studentUUID2, String organization, UUID studentUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID studentUUID, Boolean status, String designation, UUID studentUUID2, Boolean status2, String organization, UUID studentUUID3, Boolean status3);

    //show Student Child Job History Against Student UUID, Student Child UUID and
    @Query("SELECT std_child_job_history.* from std_child_job_history \n" +
            " join std_childs\n" +
            " on std_child_job_history.std_child_uuid = std_childs.uuid \n" +
            " join students \n" +
            " on std_childs.student_uuid = students.uuid \n" +
            " where std_child_job_history.uuid = :studentChildJobHistoryUUID " +
            " and std_childs.uuid  = :studentChildUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_child_job_history.deleted_at is null \n" +
            " and std_childs.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentChildJobHistoryEntity> showStudentChildJobHistoryAgainstStudentAndStudentChild(UUID studentUUID, UUID studentChildUUID, UUID studentChildJobHistoryUUID);
}
