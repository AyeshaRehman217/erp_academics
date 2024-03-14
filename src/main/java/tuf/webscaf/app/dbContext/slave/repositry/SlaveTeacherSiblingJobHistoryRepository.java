package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingJobHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingJobHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSiblingJobHistoryEntity, Long> {
    Flux<SlaveTeacherSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String occupation, String designation, String organization);

    Flux<SlaveTeacherSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, Boolean status, String designation, Boolean status2, String organization, Boolean status3);

    Flux<SlaveTeacherSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSiblingUUID, String designation, UUID teacherSiblingUUID2, String organization, UUID teacherSiblingUUID3);

    Flux<SlaveTeacherSiblingJobHistoryEntity> findAllByOccupationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String occupation, UUID teacherSiblingUUID, Boolean status, String designation, UUID teacherSiblingUUID2, Boolean status2, String organization, UUID teacherSiblingUUID3, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(String occupation, String designation, String organization);

    Mono<Long> countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String occupation, Boolean status, String designation, Boolean status2, String organization, Boolean status3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(String occupation, UUID teacherSiblingUUID, String designation, UUID teacherSiblingUUID2, String organization, UUID teacherSiblingUUID3);

    Mono<Long> countByOccupationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(String occupation, UUID teacherSiblingUUID, Boolean status, String designation, UUID teacherSiblingUUID2, Boolean status2, String organization, UUID teacherSiblingUUID3, Boolean status3);

    Mono<SlaveTeacherSiblingJobHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSiblingJobHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherSiblingJobHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    //show Teacher Sibling Job History Against Teacher UUID, Teacher Sibling UUID
    @Query("SELECT teacher_sibling_job_history.* from teacher_sibling_job_history \n" +
            " join teacher_siblings\n" +
            " on teacher_sibling_job_history.teacher_sibling_uuid = teacher_siblings.uuid \n" +
            " join teachers \n" +
            " on teacher_siblings.teacher_uuid = teachers.uuid \n" +
            " where teacher_sibling_job_history.uuid = :teacherSiblingJobHistoryUUID " +
            " and teacher_siblings.uuid  = :teacherSiblingUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_sibling_job_history.deleted_at is null \n" +
            " and teacher_siblings.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherSiblingJobHistoryEntity> showTeacherSiblingJobHistoryAgainstTeacherAndTeacherSibling(UUID teacherUUID, UUID teacherSiblingUUID, UUID teacherSiblingJobHistoryUUID);
}
