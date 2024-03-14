package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.*;

import java.util.UUID;

@Repository
public interface SlaveAilmentRepository extends ReactiveCrudRepository<SlaveAilmentEntity, Long>, SlaveCustomTeacherMotherAilmentPvtRepository, SlaveCustomTeacherChildAilmentPvtRepository,
        SlaveCustomTeacherFatherAilmentPvtRepository, SlaveCustomTeacherSiblingAilmentPvtRepository, SlaveCustomTeacherAilmentPvtRepository,
        SlaveCustomAilmentStudentPvtRepository, SlaveCustomAilmentStudentMotherPvtRepository, SlaveCustomAilmentStudentFatherPvtRepository,
        SlaveCustomAilmentStudentSiblingPvtRepository, SlaveCustomAilmentStudentGuardianPvtRepository, SlaveCustomTeacherSpouseAilmentPvtRepository,
        SlaveCustomStudentSpouseAilmentPvtRepository ,SlaveCustomStudentChildAilmentPvtRepository, SlaveCustomTeacherGuardianAilmentPvtRepository{

    Flux<SlaveAilmentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveAilmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveAilmentEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveAilmentEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveAilmentEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    /**
     * Count Number of Ailments that are not mapped Against Student Sibling  with or without status Filter
     **/
    //query used in pvt student Sibling  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_sibling_ailments_pvt\n" +
            "ON std_sibling_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_sibling_ailments_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "AND std_sibling_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingSiblingRecords(UUID stdSiblingUUID, String name, String description);

    //query used in pvt student Sibling  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_sibling_ailments_pvt\n" +
            "ON std_sibling_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_sibling_ailments_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "AND std_sibling_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentSiblingAilmentsRecordsWithStatusFilter(UUID stdSiblingUUID, String name, String description, Boolean status);

    /**
     * Count Number of Ailments Against Student Sibling  Mapping with or without status Filter
     **/
    // query used for count of mapped ailments records for given Student Sibling profile
    @Query("select count(*) from ailments\n" +
            "left join std_sibling_ailments_pvt \n" +
            "on ailments.uuid = std_sibling_ailments_pvt.ailment_uuid\n" +
            "where std_sibling_ailments_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_sibling_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSiblingAilments(UUID studentSiblingUUID, String name, String description);

    // query used for count of mapped ailments records for given Student Sibling profile
    @Query("select count(*) from ailments\n" +
            "left join std_sibling_ailments_pvt \n" +
            "on ailments.uuid = std_sibling_ailments_pvt.ailment_uuid \n" +
            "where std_sibling_ailments_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_sibling_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSiblingAilmentsWithStatus(UUID studentSiblingUUID, String name, String description, Boolean status);

    /**
     * Count Number of Ailments that are not mapped Against Student  with or without status Filter
     **/
    //query used in pvt student  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_ailments_pvt\n" +
            "ON std_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_ailments_pvt.student_uuid = :stdUUID\n" +
            "AND std_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentAilmentsRecords(UUID stdUUID, String name, String description);

    //query used in pvt student  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_ailments_pvt\n" +
            "ON std_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_ailments_pvt.student_uuid = :stdUUID\n" +
            "AND std_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentAilmentsRecordsWithStatusFilter(UUID stdUUID, String name, String description, Boolean status);

    /**
     * Count Number of Ailments Against Student  Mapping with or without status Filter
     **/
    // query used for count of mapped ailments records for given Student profile
    @Query("select count(*) from ailments\n" +
            "left join std_ailments_pvt \n" +
            "on ailments.uuid = std_ailments_pvt.ailment_uuid\n" +
            "where std_ailments_pvt.student_uuid = :stdUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentAilments(UUID stdUUID, String name, String description);

    // query used for count of mapped ailments records for given Student profile
    @Query("select count(*) from ailments\n" +
            "left join std_ailments_pvt \n" +
            "on ailments.uuid = std_ailments_pvt.ailment_uuid \n" +
            "where std_ailments_pvt.student_uuid = :stdUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentAilmentsWithStatus(UUID stdUUID, String name, String description, Boolean status);

    /**
     * Count Existing Ailments that are not mapped against student father profile yet with or without status filter
     **/
    //query used in pvt student Father  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_fth_ailments_pvt\n" +
            "ON std_fth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_fth_ailments_pvt.std_father_uuid = :studentFatherUUID\n" +
            "AND std_fth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentFatherAilmentsRecords(UUID studentFatherUUID, String name, String description);

    //query used in pvt student Father  Ailments Pvt with status filter
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_fth_ailments_pvt\n" +
            "ON std_fth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_fth_ailments_pvt.std_father_uuid = :studentFatherUUID\n" +
            "AND std_fth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentFatherAilmentsRecordsWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Student Father  ***/
    // query used for count of mapped ailments records for given student father profile
    @Query("select count(*) from ailments\n" +
            "left join std_fth_ailments_pvt\n" +
            "on ailments.uuid = std_fth_ailments_pvt.ailment_uuid\n" +
            "where std_fth_ailments_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_fth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentFatherAilments(UUID studentFatherUUID, String name, String description);

    // query used for count of mapped ailments records for given student father profile
    @Query("select count(*) from ailments\n" +
            "left join std_fth_ailments_pvt\n" +
            "on ailments.uuid = std_fth_ailments_pvt.ailment_uuid\n" +
            "where std_fth_ailments_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_fth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentFatherAilmentsWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    //query used in pvt teacher Mother  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_mth_ailments_pvt\n" +
            "ON teacher_mth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_mth_ailments_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherMotherAilmentsRecords(UUID teacherMotherUUID, String name, String description);

    //query used in pvt teacher Mother Ailments Pvt With Status
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_mth_ailments_pvt\n" +
            "ON teacher_mth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_mth_ailments_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherMotherAilmentsRecordsWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);

    //query used in pvt teacher Child  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_child_ailments_pvt\n" +
            "ON teacher_child_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_child_ailments_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "OR ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherChildAilmentsRecords(UUID teacherChildUUID, String name, String description);

    //query used in pvt teacher Child Ailments Pvt With Status
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_child_ailments_pvt\n" +
            "ON teacher_child_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_child_ailments_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL\n" +
            "AND ailments.status = :status\n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "OR ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherChildAilmentsRecordsWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    // query used for count of mapped ailments records for given teacher child
    @Query("select count(*) from ailments\n" +
            "left join teacher_child_ailments_pvt\n" +
            "on ailments.uuid = teacher_child_ailments_pvt.ailment_uuid\n" +
            "where teacher_child_ailments_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_child_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherChildAilments(UUID teacherChildProfileUUID, String name,String description);

    // query used for count of mapped ailments records for given teacher child with status
    @Query("select count(*) from ailments\n" +
            "left join teacher_child_ailments_pvt\n" +
            "on ailments.uuid = teacher_child_ailments_pvt.ailment_uuid\n" +
            "where teacher_child_ailments_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_child_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherChildAilmentsWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Teacher  ***/
    // query used for count of mapped ailments records for given teacher profile
    @Query("select count(*) from ailments\n" +
            "left join teacher_ailments_pvt\n" +
            "on ailments.uuid = teacher_ailments_pvt.ailment_uuid\n" +
            "where teacher_ailments_pvt.teacher_uuid = :teacherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherAilments(UUID teacherUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher profile
    @Query("select count(*) from ailments\n" +
            "left join teacher_ailments_pvt\n" +
            "on ailments.uuid = teacher_ailments_pvt.ailment_uuid\n" +
            "where teacher_ailments_pvt.teacher_uuid = :teacherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherAilmentsWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Teacher Spouse  ***/
    // query used for count of mapped ailments records for given teacher spouse
    @Query("select count(*) from ailments\n" +
            "left join teacher_spouse_ailments_pvt\n" +
            "on ailments.uuid = teacher_spouse_ailments_pvt.ailment_uuid\n" +
            "where teacher_spouse_ailments_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_spouse_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherSpouseAilments(UUID teacherSpouseUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher spouse with status
    @Query("select count(*) from ailments\n" +
            "left join teacher_spouse_ailments_pvt\n" +
            "on ailments.uuid = teacher_spouse_ailments_pvt.ailment_uuid\n" +
            "where teacher_spouse_ailments_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_spouse_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherSpouseAilmentsWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);

    /*** Count the Records that are not mapped/existing for Teacher Father With or Without Status Filter ***/
    //query used in pvt teacher Father Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_fth_ailments_pvt\n" +
            "ON teacher_fth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_fth_ailments_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherAilmentsRecords(UUID teacherFatherUUID, String name, String description);

    //query used in pvt teacher Father Ailments Pvt with status filter
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_fth_ailments_pvt\n" +
            "ON teacher_fth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_fth_ailments_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherAilmentsRecordsWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Teacher Father ***/
    // query used for count of mapped ailments records for given teacher father
    @Query("select count(*) from ailments\n" +
            "left join teacher_fth_ailments_pvt\n" +
            "on ailments.uuid = teacher_fth_ailments_pvt.ailment_uuid\n" +
            "where teacher_fth_ailments_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_fth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherFatherAilments(UUID teacherFatherUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher father with status
    @Query("select count(*) from ailments\n" +
            "left join teacher_fth_ailments_pvt\n" +
            "on ailments.uuid = teacher_fth_ailments_pvt.ailment_uuid\n" +
            "where teacher_fth_ailments_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_fth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherFatherAilmentsWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);

    //query used in pvt teacher Sibling Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_sibling_ailments_pvt\n" +
            "ON teacher_sibling_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_sibling_ailments_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingAilmentsRecords(UUID teacherSiblingUUID, String name, String description);

    //query used in pvt teacher Sibling Ailments Pvt With Status
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_sibling_ailments_pvt\n" +
            "ON teacher_sibling_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_sibling_ailments_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingAilmentsRecordsWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);

    // query used for count of mapped ailments records for given teacher sibling
    @Query("select count(*) from ailments\n" +
            "left join teacher_sibling_ailments_pvt\n" +
            "on ailments.uuid = teacher_sibling_ailments_pvt.ailment_uuid\n" +
            "where teacher_sibling_ailments_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_sibling_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description,'%'))")
    Mono<Long> countMappedTeacherSiblingAilments(UUID teacherSiblingUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher sibling with status
    @Query("select count(*) from ailments\n" +
            "left join teacher_sibling_ailments_pvt\n" +
            "on ailments.uuid = teacher_sibling_ailments_pvt.ailment_uuid\n" +
            "where teacher_sibling_ailments_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_sibling_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description,'%'))")
    Mono<Long> countMappedTeacherSiblingAilmentsWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);

    //query used in pvt Teacher  Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_ailments_pvt\n" +
            "ON teacher_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_ailments_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherAilmentsRecords(UUID teacherUUID, String name, String description);

    //query used in pvt Teacher  Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_ailments_pvt\n" +
            "ON teacher_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_ailments_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherAilmentsRecordsWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Spouse Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_spouse_ailments_pvt\n" +
            "ON teacher_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_spouse_ailments_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "AND teacher_spouse_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherSpouseAilmentsRecords(UUID teacherSpouseUUID, String name, String description);

    //query used in pvt Teacher Spouse Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_spouse_ailments_pvt\n" +
            "ON teacher_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_spouse_ailments_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "AND teacher_spouse_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherSpouseAilmentsRecordsWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);


    /*** Count the Records that are mapped for Teacher Mother ***/
    // query used for count of mapped ailments records for given teacher mother
    @Query("select count(*) from ailments\n" +
            "left join teacher_mth_ailments_pvt\n" +
            "on ailments.uuid = teacher_mth_ailments_pvt.ailment_uuid\n" +
            "where teacher_mth_ailments_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_mth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherMotherAilments(UUID teacherMotherUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher mother
    @Query("select count(*) from ailments\n" +
            "left join teacher_mth_ailments_pvt\n" +
            "on ailments.uuid = teacher_mth_ailments_pvt.ailment_uuid\n" +
            "where teacher_mth_ailments_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_mth_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherMotherAilmentsWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Student Mother  ***/
    // query used for count of mapped ailments records for given student mother profile
    @Query("select count(*) from ailments\n" +
            "left join std_mth_ailments_pvt \n" +
            "on ailments.uuid = std_mth_ailments_pvt.ailment_uuid \n" +
            "where std_mth_ailments_pvt.std_mother_uuid = :studentMotherUUID \n" +
            "and ailments.deleted_at is null \n" +
            "and std_mth_ailments_pvt.deleted_at is null \n" +
            "and (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentMotherAilments(UUID studentMotherUUID, String name, String description);

    // query used for count of mapped ailments records for given student mother profile
    @Query("select count(*) from ailments \n" +
            "left join std_mth_ailments_pvt \n" +
            "on ailments.uuid = std_mth_ailments_pvt.ailment_uuid \n" +
            "where std_mth_ailments_pvt.std_mother_uuid = :studentMotherUUID \n" +
            "and ailments.deleted_at is null \n" +
            "and ailments.status = :status " +
            "and std_mth_ailments_pvt.deleted_at is null \n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or " +
            "ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentMotherAilmentsWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    /*** Count the Records that are not mapped/existing for Student Mother  With or Without Status Filter ***/
    //query used in pvt student Mother  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_mth_ailments_pvt\n" +
            "ON std_mth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_mth_ailments_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentMotherAilmentsRecords(UUID studentMotherUUID, String name, String description);

    //query used in pvt student Mother  Ailments Pvt with status filter
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_mth_ailments_pvt\n" +
            "ON std_mth_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_mth_ailments_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentMotherAilmentsRecordsWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Student Guardian ***/
    // query used for count of mapped ailments records for given student guardian profile
    @Query("select count(*) from ailments\n" +
            "left join std_grd_ailments_pvt\n" +
            "on ailments.uuid = std_grd_ailments_pvt.ailment_uuid\n" +
            "where std_grd_ailments_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_grd_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentGuardianAilments(UUID studentGuardianUUID, String name, String description);

    // query used for count of mapped ailments records for given  student guardian profile
    @Query("select count(*) from ailments\n" +
            "left join std_grd_ailments_pvt\n" +
            "on ailments.uuid = std_grd_ailments_pvt.ailment_uuid\n" +
            "where std_grd_ailments_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_grd_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentGuardianAilmentsWithStatus(UUID studentGuardianUUID, String name, String description, Boolean status);

    /*** Count the Records that are not mapped/existing for Student Guardian With or Without Status Filter ***/
    //query used in pvt student Guardian  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_grd_ailments_pvt\n" +
            "ON std_grd_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_grd_ailments_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "AND std_grd_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentGuardianAilmentsRecords(UUID studentGuardianUUID, String name, String description);

    //query used in pvt student Guardian  Ailments Pvt with status filter
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_grd_ailments_pvt\n" +
            "ON std_grd_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_grd_ailments_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "AND std_grd_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentGuardianAilmentsRecordsWithStatus(UUID studentGuardianUUID, String name, String description, Boolean status);

    //query used in pvt Student Spouse  Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_spouse_ailments_pvt\n" +
            "ON std_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_spouse_ailments_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingStudentSpouseAilmentsRecordsWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    //query used in pvt Student Spouse  Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_spouse_ailments_pvt\n" +
            "ON std_spouse_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_spouse_ailments_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingStudentSpouseAilmentsRecords(UUID stdSpouseUUID, String name, String description);

    /*** Count the Records that are mapped for Student Spouse  ***/
    // query used for count of mapped ailments records for given teacher spouse profile
    @Query("select count(*) from ailments\n" +
            "left join std_spouse_ailments_pvt\n" +
            "on ailments.uuid = std_spouse_ailments_pvt.ailment_uuid\n" +
            "where std_spouse_ailments_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_spouse_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSpouseAilments(UUID stdSpouseUUID, String name, String description);

    // query used for count of mapped ailments records for given student spouse profile
    @Query("select count(*) from ailments\n" +
            "left join std_spouse_ailments_pvt\n" +
            "on ailments.uuid = std_spouse_ailments_pvt.ailment_uuid\n" +
            "where std_spouse_ailments_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_spouse_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSpouseAilmentsWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    /**
     * Count Existing Ailments that are not mapped against student child profile yet with or without status filter
     **/
    //query used in pvt student Child  Ailments Pvt
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_child_ailments_pvt\n" +
            "ON std_child_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_child_ailments_pvt.std_child_uuid = :studentChildUUID\n" +
            "AND std_child_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentChildAilmentsRecords(UUID studentChildUUID, String name, String description);

    //query used in pvt student Child Ailments Pvt with status filter
    @Query("SELECT count(*) FROM ailments\n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN std_child_ailments_pvt\n" +
            "ON std_child_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE std_child_ailments_pvt.std_child_uuid = :studentChildUUID\n" +
            "AND std_child_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or" +
            " ailments.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentChildAilmentsRecordsWithStatus(UUID studentChildUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Student Child  ***/
    // query used for count of mapped ailments records for given student child profile
    @Query("select count(*) from ailments\n" +
            "left join std_child_ailments_pvt\n" +
            "on ailments.uuid = std_child_ailments_pvt.ailment_uuid\n" +
            "where std_child_ailments_pvt.std_child_uuid = :studentChildUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and std_child_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentChildAilments(UUID studentChildUUID, String name, String description);

    // query used for count of mapped ailments records for given student child profile
    @Query("select count(*) from ailments\n" +
            "left join std_child_ailments_pvt\n" +
            "on ailments.uuid = std_child_ailments_pvt.ailment_uuid\n" +
            "where std_child_ailments_pvt.std_child_uuid = :studentChildUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and std_child_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentChildAilmentsWithStatus(UUID studentChildUUID, String name, String description, Boolean status);

    /**
     * Count Existing Ailments that are not mapped against teacher guardian yet with or without status filter
     **/
    //query used in pvt Teacher Guardian Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_grd_ailments_pvt\n" +
            "ON teacher_grd_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_grd_ailments_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND (ailments.name ILIKE concat('%',:name,'%') " +
            "or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherGuardianAilmentsRecords(UUID teacherGuardianUUID, String name, String description);

    //query used in pvt Teacher Guardian Ailments Pvt
    @Query("SELECT count(*) FROM ailments \n" +
            "WHERE ailments.uuid NOT IN(\n" +
            "SELECT ailments.uuid FROM ailments\n" +
            "LEFT JOIN teacher_grd_ailments_pvt\n" +
            "ON teacher_grd_ailments_pvt.ailment_uuid = ailments.uuid \n" +
            "WHERE teacher_grd_ailments_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_ailments_pvt.deleted_at IS NULL\n" +
            "AND ailments.deleted_at IS NULL )\n" +
            "AND ailments.deleted_at IS NULL " +
            "AND ailments.status = :status \n" +
            "AND (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE concat('%',:description ,'%')) \n")
    Mono<Long> countExistingTeacherGuardianAilmentsRecordsWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);

    /*** Count the Records that are mapped for Teacher Guardian  ***/
    // query used for count of mapped ailments records for given teacher guardian
    @Query("select count(*) from ailments\n" +
            "left join teacher_grd_ailments_pvt\n" +
            "on ailments.uuid = teacher_grd_ailments_pvt.ailment_uuid\n" +
            "where teacher_grd_ailments_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and teacher_grd_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherGuardianAilments(UUID teacherGuardianUUID, String name, String description);

    // query used for count of mapped ailments records for given teacher guardian
    @Query("select count(*) from ailments\n" +
            "left join teacher_grd_ailments_pvt\n" +
            "on ailments.uuid = teacher_grd_ailments_pvt.ailment_uuid\n" +
            "where teacher_grd_ailments_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and ailments.deleted_at is null\n" +
            "and ailments.status = :status " +
            "and teacher_grd_ailments_pvt.deleted_at is null\n" +
            "and (ailments.name ILIKE concat('%',:name,'%') or ailments.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherGuardianAilmentsWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);
}
