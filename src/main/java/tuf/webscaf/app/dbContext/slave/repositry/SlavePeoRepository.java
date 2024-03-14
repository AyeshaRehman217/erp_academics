package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPeoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPloPeoPvtRepository;

import java.util.UUID;

@Repository
public interface SlavePeoRepository extends ReactiveCrudRepository<SlavePeoEntity, Long>, SlaveCustomPloPeoPvtRepository, SlaveCustomPeoRepository {

    Mono<SlavePeoEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlavePeoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlavePeoEntity> findAllByCodeContainingIgnoreCaseAndDeletedAtIsNullOrNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String code, String name, String description);

    Mono<Long> countByCodeContainingIgnoreCaseAndDeletedAtIsNullOrNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String code, String name, String description);

    Flux<SlavePeoEntity> findAllByCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String code, Boolean status, String name, Boolean status1, String description, Boolean status2);

    Mono<Long> countByCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String code, Boolean status, String name, Boolean status1, String description, Boolean status2);

    /**
     * Index All Records where status and Department UUID Both Are present
     **/
    Flux<SlavePeoEntity> findAllByCodeContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrNameContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNull(Pageable pageable, String code, Boolean status, UUID dept1, String name, Boolean status1, UUID dept2, String description, Boolean status2, UUID dept3);

    Mono<Long> countByCodeContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrNameContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDepartmentUUIDAndDeletedAtIsNull(String code, Boolean status, UUID dept1, String name, Boolean status1, UUID dept2, String description, Boolean status2, UUID dept3);

    /**
     * Index All Records where Department is present
     **/
    Flux<SlavePeoEntity> findAllByCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrNameContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(Pageable pageable, String code, UUID dept1, String name, UUID dept2, String description, UUID dept3);

    Mono<Long> countByCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrNameContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String code, UUID dept1, String name, UUID dept2, String description, UUID dept3);

    /**
     * Count PEO's that are not mapped/existing yet for Given PLO UUID with and without Status Filter
     **/
    //query used in pvt PLO PEO Pvt
    @Query("SELECT count(*) FROM peos\n" +
            "WHERE peos.uuid NOT IN(\n" +
            "SELECT peos.uuid FROM peos\n" +
            "LEFT JOIN plo_peos_pvt\n" +
            "ON plo_peos_pvt.peo_uuid = peos.uuid \n" +
            "WHERE plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "AND plo_peos_pvt.deleted_at IS NULL\n" +
            "AND peos.deleted_at IS NULL )\n" +
            "AND peos.deleted_at IS NULL " +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedPloPeoRecords(UUID ploUUID, String title, String name, String code, String description);

    //query used in pvt PLO PEO Pvt
    @Query("SELECT count(*) FROM peos\n" +
            "WHERE peos.uuid NOT IN(\n" +
            "SELECT peos.uuid FROM peos\n" +
            "LEFT JOIN plo_peos_pvt\n" +
            "ON plo_peos_pvt.peo_uuid = peos.uuid \n" +
            "WHERE plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "AND plo_peos_pvt.deleted_at IS NULL\n" +
            "AND peos.deleted_at IS NULL )\n" +
            "AND peos.deleted_at IS NULL " +
            "AND peos.status = :status \n" +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedPloPeoRecordsWithStatus(UUID ploUUID, String title, String name, String code, String description, Boolean status);

    @Query("SELECT count(*) FROM peos\n" +
            "WHERE peos.uuid NOT IN(\n" +
            "SELECT peos.uuid FROM peos\n" +
            "LEFT JOIN plo_peos_pvt\n" +
            "ON plo_peos_pvt.peo_uuid = peos.uuid \n" +
            "WHERE plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "AND plo_peos_pvt.deleted_at IS NULL\n" +
            "AND peos.deleted_at IS NULL )\n" +
            "AND peos.status = :status \n" +
            "AND peos.department_uuid = :departmentUUID \n" +
            "AND peos.deleted_at IS NULL " +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedPloPeoRecordsWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, String title, String name, String code, String description, Boolean status);

    @Query("SELECT count(*) FROM peos\n" +
            "WHERE peos.uuid NOT IN(\n" +
            "SELECT peos.uuid FROM peos\n" +
            "LEFT JOIN plo_peos_pvt\n" +
            "ON plo_peos_pvt.peo_uuid = peos.uuid \n" +
            "WHERE plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "AND plo_peos_pvt.deleted_at IS NULL\n" +
            "AND peos.deleted_at IS NULL )\n" +
            "AND peos.department_uuid = :departmentUUID \n" +
            "AND peos.deleted_at IS NULL " +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countUnMappedPloPeoRecordsWithDepartment(UUID departmentUUID, UUID ploUUID, String title, String name, String code, String description);

    /**
     * Count PEO that are mapped for Given PLO UUID with and without Status Filter
     **/
    @Query("select count(*) from peos\n" +
            "left join plo_peos_pvt\n" +
            "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "where plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "and peos.deleted_at is null\n" +
            "and plo_peos_pvt.deleted_at is null\n" +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedPloPeo(UUID ploUUID, String title, String name, String code, String description);

    // query used for count of mapped peo records for given Plo UUID
    @Query("select count(*) from peos\n" +
            "left join plo_peos_pvt\n" +
            "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "where plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "and peos.deleted_at is null\n" +
            "and peos.status = :status " +
            "and plo_peos_pvt.deleted_at is null\n" +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedPloPeoWithStatus(UUID ploUUID, String title, String name, String code, String description, Boolean status);

    // query used for count of mapped peo records for given Plo UUID and department UUID and status filter
    @Query("select count(*) from peos\n" +
            "left join plo_peos_pvt\n" +
            "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "where plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "and peos.status = :status " +
            "and peos.department_uuid = :departmentUUID " +
            "and peos.deleted_at is null\n" +
            "and plo_peos_pvt.deleted_at is null\n" +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            " OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedPloPeoListWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, Boolean status, String title, String name, String code, String description);

    // query used for count of mapped peo records for given Plo UUID and department UUID
    @Query("select count(*) from peos\n" +
            "left join plo_peos_pvt\n" +
            "on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "where plo_peos_pvt.plo_uuid = :ploUUID\n" +
            "and peos.department_uuid = :departmentUUID " +
            "and peos.deleted_at is null\n" +
            "and plo_peos_pvt.deleted_at is null\n" +
            " AND (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            "  OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedPloPeoListWithDepartment(UUID departmentUUID, UUID ploUUID, String title, String name, String code, String description);


    /**
     * Count All Peo Records With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from peos \n" +
            " where peos.deleted_at is null\n" +
            " and peos.status= :status\n" +
            " and \n" +
            " ( peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            "  OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countPeoAgainstStatus(Boolean status, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from peos \n" +
            " where peos.deleted_at is null \n" +
            " and \n" +
            " (peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            "  OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countPeoWithoutStatus(String name, String code, String description);


    /**
     * Count All Peo Records Against department & With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from peos \n" +
            " where peos.deleted_at is null\n" +
            " and peos.department_uuid= :departmentUUID\n" +
            " and peos.status= :status\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            "  OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countPeoAgainstDepartmentAndStatus(UUID departmentUUID, Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from peos \n" +
            " where peos.deleted_at is null\n" +
            " and peos.department_uuid= :departmentUUID\n" +
            " and \n" +
            " (CASE \n" +
            " WHEN LENGTH(peos.name) > 0 THEN concat_ws('|',peos.name,peos.code,peos.description) \n" +
            " ELSE concat_ws('|',peos.code,peos.description) \n" +
            " END ILIKE concat('%',:title,'%') " +
            " OR peos.name ILIKE concat('%',:name,'%') \n" +
            " OR peos.code ILIKE concat('%',:code,'%') \n" +
            "  OR peos.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countPeoAgainstDepartment(UUID departmentUUID, String title, String name, String code, String description);
}
