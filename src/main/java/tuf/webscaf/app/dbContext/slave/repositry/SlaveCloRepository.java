package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloRepository;

import java.util.UUID;

@Repository
public interface SlaveCloRepository extends ReactiveCrudRepository<SlaveCloEntity, Long>, SlaveCustomCloRepository {
    Mono<SlaveCloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCloEntity> findAllByCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String code, String description);

    Mono<Long> countByCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String code, String description);

    Flux<SlaveCloEntity> findAllByCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String code, Boolean status, String description, Boolean status2);

    Mono<Long> countByCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String code, Boolean status, String description, Boolean status2);

    Flux<SlaveCloEntity> findAllByCodeContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNull(Pageable pageable, String code, Boolean status, UUID departmentUUID, String description, Boolean status2, UUID departmentUUID2);

    Mono<Long> countByCodeContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNull(String code, Boolean status, UUID departmentUUID, String description, Boolean status2, UUID departmentUUID2);

    Flux<SlaveCloEntity> findAllByCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(Pageable pageable, String codeUUID, UUID departmentUUID, String description, UUID departmentUUID2);

    Mono<Long> countByCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String codeUUID, UUID departmentUUID, String description, UUID departmentUUID2);

    //query used in Clo Subject Obe Pvt Handler
    @Query("SELECT count(*) FROM clos\n" +
            "WHERE clos.uuid NOT IN(\n" +
            "SELECT clos.uuid FROM clos\n" +
            "LEFT JOIN subject_obe_clos_pvt\n" +
            "ON subject_obe_clos_pvt.clo_uuid = clos.uuid \n" +
            "WHERE subject_obe_clos_pvt.subject_obe_uuid = :subjectObeUUID\n" +
            "AND subject_obe_clos_pvt.deleted_at IS NULL\n" +
            "AND clos.deleted_at IS NULL )\n" +
            "AND clos.deleted_at IS NULL " +
            " and (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedCloSubjectObeRecords(UUID subjectObeUUID, String title, String name, String code, String description);

    //query used in Clo Subject Obe Pvt Handler With Status
    @Query("SELECT count(*) FROM clos\n" +
            "WHERE clos.uuid NOT IN(\n" +
            "SELECT clos.uuid FROM clos\n" +
            "LEFT JOIN subject_obe_clos_pvt\n" +
            "ON subject_obe_clos_pvt.clo_uuid = clos.uuid \n" +
            "WHERE subject_obe_clos_pvt.subject_obe_uuid = :subjectObeUUID\n" +
            "AND subject_obe_clos_pvt.deleted_at IS NULL\n" +
            "AND clos.deleted_at IS NULL )\n" +
            "AND clos.deleted_at IS NULL " +
            "AND clos.status = :status " +
            " and (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedCloSubjectObeRecordsWithStatus(UUID subjectObeUUID, String title, String name, String code, String description, Boolean status);

    // query used for count of mapped clos records for given subject obe
    @Query("select count(*) from clos\n" +
            "left join subject_obe_clos_pvt\n" +
            "on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
            "where subject_obe_clos_pvt.subject_obe_uuid = :subjectObeUUID\n" +
            "and clos.deleted_at is null\n" +
            "and subject_obe_clos_pvt.deleted_at is null\n" +
            " and (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedCloSubjectObeRecords(UUID subjectObeUUID, String title, String name, String code, String description);

    // query used for count of mapped clos records for given subject obe
    @Query("select count(*) from clos\n" +
            "left join subject_obe_clos_pvt\n" +
            "on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
            "where subject_obe_clos_pvt.subject_obe_uuid = :subjectObeUUID\n" +
            "and clos.deleted_at is null\n" +
            "and clos.status = :status " +
            "and subject_obe_clos_pvt.deleted_at is null\n" +
            " and (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedCloSubjectObeRecordsWithStatus(UUID subjectObeUUID, String title, String name, String code, String description, Boolean status);


    /**
     * Count All Clo Records With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.status= :status\n" +
            " and \n" +
            " ( CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstStatus(Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null \n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloWithoutStatus(String title, String name, String code, String description);


    /**
     * Count All Clo Records Against department & With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.department_uuid= :departmentUUID\n" +
            " and clos.status= :status\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstDepartmentAndStatus(UUID departmentUUID, Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.department_uuid= :departmentUUID\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstDepartment(UUID departmentUUID, String title, String name, String code, String description);

    /**
     * Count All Clo Records Against Emphasis & With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.emphasis_uuid= :emphasisUUID\n" +
            " and clos.status= :status\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstEmphasisAndStatus(UUID emphasisUUID, Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null \n" +
            " and clos.emphasis_uuid= :emphasisUUID \n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstEmphasis(UUID emphasisUUID, String title, String name, String code, String description);

    /**
     * Count All Clo Records Against Department & Emphasis With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.emphasis_uuid= :emphasisUUID\n" +
            " and clos.department_uuid= :departmentUUID\n" +
            " and clos.status= :status\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstEmphasisAndDepartmentAndStatus(UUID emphasisUUID, UUID departmentUUID, Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from clos \n" +
            " where clos.deleted_at is null\n" +
            " and clos.emphasis_uuid= :emphasisUUID\n" +
            " and clos.department_uuid= :departmentUUID\n" +
            " and \n" +
            " ( CASE \n" +
            " WHEN LENGTH(clos.name) > 0 THEN concat_ws('|',clos.name,clos.code,clos.description) \n" +
            " ELSE concat_ws('|',clos.code,clos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR clos.name ILIKE concat('%',:name,'%') \n" +
            " OR clos.code ILIKE concat('%',:code,'%') \n" +
            "  OR clos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countCloAgainstEmphasisAndDepartment(UUID emphasisUUID, UUID departmentUUID, String title, String name, String code, String description);
}
