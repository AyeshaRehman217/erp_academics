package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.*;

import java.util.UUID;

@Repository
public interface SlaveNationalityRepository extends ReactiveCrudRepository<SlaveNationalityEntity, Long>, SlaveCustomNationalityStudentGuardianPvtRepository,
        SlaveCustomTeacherFatherNationalityPvtRepository, SlaveCustomStudentNationalityPvtRepository, SlaveCustomStudentMotherNationalityPvtRepository,
        SlaveCustomTeacherMotherNationalityPvtRepository, SlaveCustomTeacherNationalityPvtRepository, SlaveCustomTeacherSiblingNationalityPvtRepository,
        SlaveCustomTeacherChildNationalityPvtRepository, SlaveCustomNationalityStudentSiblingPvtRepository, SlaveCustomStudentFatherNationalityPvtRepository,
        SlaveCustomTeacherSpouseNationalityPvtRepository, SlaveCustomStudentSpouseNationalityPvtRepository, SlaveCustomStudentChildNationalityPvtRepository,
        SlaveCustomTeacherGuardianNationalityPvtRepository {

    Mono<SlaveNationalityEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveNationalityEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveNationalityEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    //Find By Country uuid In Config Module
    Mono<SlaveNationalityEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    Mono<SlaveNationalityEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Count Nationalities that are not mapped against Student Guardian with and without status filter
     **/
    //query used in pvt student Guardian  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_grd_nationalities_pvt\n" +
            "ON std_grd_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_grd_nationalities_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "AND std_grd_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingGuardianNationalityRecords(UUID stdGuardianUUID, String name, String description);

    //query used in pvt Student Guardian Nationalities Pvt with status Filter
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_grd_nationalities_pvt\n" +
            "ON std_grd_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_grd_nationalities_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "AND std_grd_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentGuardianNationalityRecordsWithStatus(UUID stdGuardianUUID, String name, String description, Boolean status);

    /***Count Nationalities that are not mapped against Student Father  with Status Filter and Without filter**/
    //query used in pvt student Father  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_fth_nationalities_pvt\n" +
            "ON std_fth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_fth_nationalities_pvt.std_father_uuid = :studentFatherUUID\n" +
            "AND std_fth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentFatherNationalityRecords(UUID studentFatherUUID, String name, String description);


    //query used in pvt Student Father  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_fth_nationalities_pvt\n" +
            "ON std_fth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_fth_nationalities_pvt.std_father_uuid = :studentFatherUUID\n" +
            "AND std_fth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentFatherNationalityRecordsWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    //query used in pvt teacher Father Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_fth_nationalities_pvt\n" +
            "ON teacher_fth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_fth_nationalities_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherNationalityRecords(UUID teacherFatherUUID, String name, String description);

    //query used in pvt teacher Father Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_fth_nationalities_pvt\n" +
            "ON teacher_fth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_fth_nationalities_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherNationalityRecordsWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);

    /**
     * Count Existing Nationality list that are not mapped against student Mother  with Status and Without Status Filter
     **/
    //query used in pvt Student Mother  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_mth_nationalities_pvt\n" +
            "ON std_mth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_mth_nationalities_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or " +
            "nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentMotherNationalityRecords(UUID studentMotherUUID, String name, String description);


    //query used in pvt Student Mother  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_mth_nationalities_pvt\n" +
            "ON std_mth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_mth_nationalities_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentMotherNationalityRecordsWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    /**
     * Count Existing Nationalities against Teacher Mother with or Without Status Filter
     **/
    //query used in pvt Teacher Mother Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_mth_nationalities_pvt\n" +
            "ON teacher_mth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_mth_nationalities_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or " +
            "nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherMotherNationalityRecords(UUID teacherMotherUUID, String name, String description);

    //query used in pvt Teacher Mother Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_mth_nationalities_pvt\n" +
            "ON teacher_mth_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_mth_nationalities_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherMotherNationalityRecordsWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped against student Mother  with or without status filter
     **/
    @Query("select count(*) from nationalities\n" +
            "left join std_mth_nationalities_pvt\n" +
            "on nationalities.uuid = std_mth_nationalities_pvt.nationality_uuid\n" +
            "where std_mth_nationalities_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_mth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentMotherNationalities(UUID studentMotherUUID, String name, String description);

    // query used for count of mapped nationalities records for given student mother profile
    @Query("select count(*) from nationalities\n" +
            "left join std_mth_nationalities_pvt\n" +
            "on nationalities.uuid = std_mth_nationalities_pvt.nationality_uuid\n" +
            "where std_mth_nationalities_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_mth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentMotherNationalitiesWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_nationalities_pvt\n" +
            "ON teacher_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_nationalities_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherNationalityRecords(UUID teacherUUID, String name, String description);

    //query used in pvt Teacher Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_nationalities_pvt\n" +
            "ON teacher_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_nationalities_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status \n" +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherNationalityRecordsWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    // query used in pvt Teacher Spouse Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_spouse_nationalities_pvt\n" +
            "ON teacher_spouse_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_spouse_nationalities_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "AND teacher_spouse_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherSpouseNationalityRecords(UUID teacherSpouseUUID, String name, String description);

    //query used in pvt Teacher Spouse Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_spouse_nationalities_pvt\n" +
            "ON teacher_spouse_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_spouse_nationalities_pvt.teacher_spouse_uuid = :teacher_spouseSpouseUUID\n" +
            "AND teacher_spouse_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status \n" +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherSpouseNationalityRecordsWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Sibling Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_sibling_nationalities_pvt\n" +
            "ON teacher_sibling_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_sibling_nationalities_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingNationalityRecords(UUID teacherSiblingUUID, String name, String description);

    //query used in pvt Teacher Sibling Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_sibling_nationalities_pvt\n" +
            "ON teacher_sibling_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_sibling_nationalities_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingNationalityRecordsWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);

    // query used for count of mapped nationalities records for given teacher sibling
    @Query("select count(*) from nationalities\n" +
            "left join teacher_sibling_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_sibling_nationalities_pvt.nationality_uuid\n" +
            "where teacher_sibling_nationalities_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_sibling_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedTeacherSiblingNationalities(UUID teacherSiblingUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher sibling with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_sibling_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_sibling_nationalities_pvt.nationality_uuid\n" +
            "where teacher_sibling_nationalities_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_sibling_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedTeacherSiblingNationalitiesWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Child  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_child_nationalities_pvt\n" +
            "ON teacher_child_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_child_nationalities_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%') )\n")
    Mono<Long> countExistingTeacherChildNationalityRecords(UUID teacherChildUUID, String name, String description);

    //query used in pvt Teacher Child Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_child_nationalities_pvt\n" +
            "ON teacher_child_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_child_nationalities_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%') )\n")
    Mono<Long> countExistingTeacherChildNationalityRecordsWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    // query used for count of mapped nationalities records for given teacher child
    @Query("select count(*) from nationalities\n" +
            "left join teacher_child_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_child_nationalities_pvt.nationality_uuid\n" +
            "where teacher_child_nationalities_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_child_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherChildNationalities(UUID teacherChildUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher child with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_child_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_child_nationalities_pvt.nationality_uuid\n" +
            "where teacher_child_nationalities_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_child_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherChildNationalitiesWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    /**
     * Count Number of Hobbies that are not mapped against Student Sibling
     **/
    //query used in pvt Student Sibling  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_sibling_nationalities_pvt\n" +
            "ON std_sibling_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_sibling_nationalities_pvt.std_sibling_uuid = :studentSiblingUUID\n" +
            "AND std_sibling_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentSiblingNationalityRecords(UUID studentSiblingUUID, String name, String description);

    //query used in pvt Student Sibling  Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_sibling_nationalities_pvt\n" +
            "ON std_sibling_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_sibling_nationalities_pvt.std_sibling_uuid = :studentSiblingUUID\n" +
            "AND std_sibling_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentSiblingNationalityRecordsWithStatus(UUID studentSiblingUUID, String name, String description, Boolean status);

    /**
     * Count Nationalities that are mapped for Student Sibling  with and without status filter
     **/
    // query used for count of mapped nationalities records for given student Sibling profile
    @Query("select count(*) from nationalities\n" +
            "left join std_sibling_nationalities_pvt\n" +
            "on nationalities.uuid = std_sibling_nationalities_pvt.nationality_uuid\n" +
            "where std_sibling_nationalities_pvt.std_sibling_uuid = :studentSiblingUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_sibling_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%'))")
    Mono<Long> countMappedStudentSiblingNationalities(UUID studentSiblingUUID, String name, String description);

    // query used for count of mapped nationalities records for given student Sibling profile
    @Query("select count(*) from nationalities\n" +
            "left join std_sibling_nationalities_pvt\n" +
            "on nationalities.uuid = std_sibling_nationalities_pvt.nationality_uuid\n" +
            "where std_sibling_nationalities_pvt.std_sibling_uuid = :studentSiblingUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_sibling_nationalities_pvt.deleted_at is null\n" +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentSiblingNationalitiesWithStatus(UUID studentSiblingUUID, String name, String description, Boolean status);


    /**
     * Count Number of Nationalities that are mapped for a given Teacher
     **/
    // query used for count of mapped nationalities records for given teacher
    @Query("select count(*) from nationalities\n" +
            "left join teacher_nationalities_pvt \n" +
            "on nationalities.uuid = teacher_nationalities_pvt.nationality_uuid\n" +
            "where teacher_nationalities_pvt.teacher_uuid = :teacherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherNationalities(UUID teacherUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher
    @Query("select count(*) from nationalities\n" +
            "left join teacher_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_nationalities_pvt.nationality_uuid\n" +
            "where teacher_nationalities_pvt.teacher_uuid = :teacherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherNationalitiesWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped for a given Teacher Spouse
     **/
    // query used for count of mapped nationalities records for given teacher spouse
    @Query("select count(*) from nationalities\n" +
            "left join teacher_spouse_nationalities_pvt \n" +
            "on nationalities.uuid = teacher_spouse_nationalities_pvt.nationality_uuid\n" +
            "where teacher_spouse_nationalities_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_spouse_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherSpouseNationalities(UUID teacherSpouseUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher spouse with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_spouse_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_spouse_nationalities_pvt.nationality_uuid\n" +
            "where teacher_spouse_nationalities_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_spouse_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherSpouseNationalitiesWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities for a given Teacher Father that are mapped
     **/
    // query used for count of mapped nationalities records for given teacher Father
    @Query("select count(*) from nationalities\n" +
            "left join teacher_fth_nationalities_pvt \n" +
            "on nationalities.uuid = teacher_fth_nationalities_pvt.nationality_uuid\n" +
            "where teacher_fth_nationalities_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_fth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherFatherNationalities(UUID teacherFatherUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher Father with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_fth_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_fth_nationalities_pvt.nationality_uuid\n" +
            "where teacher_fth_nationalities_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_fth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherFatherNationalitiesWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);


    /**
     * Count Number of Nationalities for a given Teacher Mother that are mapped
     **/
    // query used for count of mapped nationalities records for given teacher Mother
    @Query("select count(*) from nationalities \n" +
            "left join teacher_mth_nationalities_pvt \n" +
            "on nationalities.uuid = teacher_mth_nationalities_pvt.nationality_uuid \n" +
            "where teacher_mth_nationalities_pvt.teacher_mother_uuid = :teacherMotherUUID \n" +
            "and nationalities.deleted_at is null \n" +
            "and teacher_mth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherMotherNationalities(UUID teacherMotherUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher Mother with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_mth_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_mth_nationalities_pvt.nationality_uuid\n" +
            "where teacher_mth_nationalities_pvt.teacher_mother_uuid = :teacherMotherUUID \n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_mth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherMotherNationalitiesWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);


    /**
     * Count Number of Nationalities that are mapped for a given Student
     **/
    // query used for count of mapped nationalities records for given Student profile
    @Query("select count(*) from nationalities\n" +
            "left join std_nationalities_pvt \n" +
            "on nationalities.uuid = std_nationalities_pvt.nationality_uuid\n" +
            "where std_nationalities_pvt.student_uuid = :studentUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or " +
            "nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentNationalities(UUID studentUUID, String name, String description);

    // query used for count of mapped nationalities records for given Student profile
    @Query("select count(*) from nationalities\n" +
            "left join std_nationalities_pvt\n" +
            "on nationalities.uuid = std_nationalities_pvt.nationality_uuid\n" +
            "where std_nationalities_pvt.student_uuid = :studentUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or " +
            "nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentNationalitiesWithStatus(UUID studentUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are not mapped for a given Student
     **/
    //query used in pvt Student  Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_nationalities_pvt\n" +
            "ON std_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_nationalities_pvt.student_uuid = :studentUUID\n" +
            "AND std_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentNationalityRecords(UUID studentUUID, String name, String description);

    //query used in pvt Student  Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_nationalities_pvt\n" +
            "ON std_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_nationalities_pvt.student_uuid = :studentUUID\n" +
            "AND std_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "OR nationalities.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentNationalityRecordsWithStatus(UUID studentUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped against student Father  with or without status filter
     **/
    @Query("select count(*) from nationalities\n" +
            "left join std_fth_nationalities_pvt\n" +
            "on nationalities.uuid = std_fth_nationalities_pvt.nationality_uuid\n" +
            "where std_fth_nationalities_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_fth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentFatherNationalities(UUID studentFatherUUID, String name, String description);

    // query used for count of mapped nationalities records for given student father profile
    @Query("select count(*) from nationalities\n" +
            "left join std_fth_nationalities_pvt\n" +
            "on nationalities.uuid = std_fth_nationalities_pvt.nationality_uuid\n" +
            "where std_fth_nationalities_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_fth_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentFatherNationalitiesWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped against student Guardian  with or without status filter
     **/
    @Query("select count(*) from nationalities\n" +
            "left join std_grd_nationalities_pvt\n" +
            "on nationalities.uuid = std_grd_nationalities_pvt.nationality_uuid\n" +
            "where std_grd_nationalities_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_grd_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentGuardianNationalities(UUID studentGuardianUUID, String name, String description);

    // query used for count of mapped nationalities records for given student mother profile
    @Query("select count(*) from nationalities\n" +
            "left join std_grd_nationalities_pvt\n" +
            "on nationalities.uuid = std_grd_nationalities_pvt.nationality_uuid\n" +
            "where std_grd_nationalities_pvt.std_guardian_uuid = :studentGuardianUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_grd_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentGuardianNationalitiesWithStatus(UUID studentGuardianUUID, String name, String description, Boolean status);

    //    query used in pvt Student Spouse Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_spouse_nationalities_pvt\n" +
            "ON std_spouse_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_spouse_nationalities_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentSpouseNationalityRecords(UUID stdSpouseUUID, String name, String description);

    //query used in pvt Student Spouse Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_spouse_nationalities_pvt\n" +
            "ON std_spouse_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_spouse_nationalities_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status \n" +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentSpouseNationalityRecordsWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped for a given Student Spouse
     **/
    // query used for count of mapped nationalities records for given std spouse
    @Query("select count(*) from nationalities\n" +
            "left join std_spouse_nationalities_pvt \n" +
            "on nationalities.uuid = std_spouse_nationalities_pvt.nationality_uuid\n" +
            "where std_spouse_nationalities_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_spouse_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSpouseNationalities(UUID stdSpouseUUID, String name, String description);

    // query used for count of mapped nationalities records for given std spouse with status
    @Query("select count(*) from nationalities\n" +
            "left join std_spouse_nationalities_pvt\n" +
            "on nationalities.uuid = std_spouse_nationalities_pvt.nationality_uuid\n" +
            "where std_spouse_nationalities_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_spouse_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedStudentSpouseNationalitiesWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    /***Count Nationalities that are not mapped against Student Father  with Status Filter and Without filter**/
    //query used in pvt student Child Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_child_nationalities_pvt\n" +
            "ON std_child_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_child_nationalities_pvt.std_child_uuid = :studentChildUUID\n" +
            "AND std_child_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentChildNationalityRecords(UUID studentChildUUID, String name, String description);


    //query used in pvt Student Child Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN std_child_nationalities_pvt\n" +
            "ON std_child_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE std_child_nationalities_pvt.std_child_uuid = :studentChildUUID\n" +
            "AND std_child_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') " +
            "or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentChildNationalityRecordsWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped against student Child  with or without status filter
     **/
    @Query("select count(*) from nationalities\n" +
            "left join std_child_nationalities_pvt\n" +
            "on nationalities.uuid = std_child_nationalities_pvt.nationality_uuid\n" +
            "where std_child_nationalities_pvt.std_child_uuid = :studentChildUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and std_child_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentChildNationalities(UUID studentChildUUID, String name, String description);

    // query used for count of mapped nationalities records for given student child profile
    @Query("select count(*) from nationalities\n" +
            "left join std_child_nationalities_pvt\n" +
            "on nationalities.uuid = std_child_nationalities_pvt.nationality_uuid\n" +
            "where std_child_nationalities_pvt.std_child_uuid = :studentChildUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and std_child_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ilike concat('%',:name,'%') " +
            "or nationalities.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedStudentChildNationalitiesWithStatus(UUID studentChildUUID, String name, String description, Boolean status);

    // query used in pvt Teacher Guardian Nationalities Pvt
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_grd_nationalities_pvt\n" +
            "ON teacher_grd_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_grd_nationalities_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherGuardianNationalityRecords(UUID teacherGuardianUUID, String name, String description);

    //query used in pvt Teacher Guardian Nationalities Pvt With Status
    @Query("SELECT count(*) FROM nationalities\n" +
            "WHERE nationalities.uuid NOT IN(\n" +
            "SELECT nationalities.uuid FROM nationalities\n" +
            "LEFT JOIN teacher_grd_nationalities_pvt\n" +
            "ON teacher_grd_nationalities_pvt.nationality_uuid = nationalities.uuid \n" +
            "WHERE teacher_grd_nationalities_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_nationalities_pvt.deleted_at IS NULL\n" +
            "AND nationalities.deleted_at IS NULL )\n" +
            "AND nationalities.deleted_at IS NULL " +
            "AND nationalities.status = :status \n" +
            "AND (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherGuardianNationalityRecordsWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);

    /**
     * Count Number of Nationalities that are mapped for a given Teacher Guardian
     **/
    // query used for count of mapped nationalities records for given teacher guardian
    @Query("select count(*) from nationalities\n" +
            "left join teacher_grd_nationalities_pvt \n" +
            "on nationalities.uuid = teacher_grd_nationalities_pvt.nationality_uuid\n" +
            "where teacher_grd_nationalities_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and teacher_grd_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherGuardianNationalities(UUID teacherGuardianUUID, String name, String description);

    // query used for count of mapped nationalities records for given teacher guardian with status
    @Query("select count(*) from nationalities\n" +
            "left join teacher_grd_nationalities_pvt\n" +
            "on nationalities.uuid = teacher_grd_nationalities_pvt.nationality_uuid\n" +
            "where teacher_grd_nationalities_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and nationalities.deleted_at is null\n" +
            "and nationalities.status = :status " +
            "and teacher_grd_nationalities_pvt.deleted_at is null\n" +
            "and (nationalities.name ILIKE concat('%',:name,'%') or nationalities.description ILIKE  concat('%',:description,'%') )")
    Mono<Long> countMappedTeacherGuardianNationalitiesWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);
}
