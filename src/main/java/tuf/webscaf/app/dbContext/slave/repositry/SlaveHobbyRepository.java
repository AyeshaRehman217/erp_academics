package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.*;

import java.util.UUID;

@Repository
public interface SlaveHobbyRepository extends ReactiveCrudRepository<SlaveHobbyEntity, Long>, SlaveCustomTeacherFatherHobbyPvtRepository,
        SlaveCustomStudentMotherHobbyPvtRepository, SlaveCustomStudentFatherHobbyPvtRepository, SlaveCustomTeacherSiblingHobbyPvtRepository,
        SlaveCustomTeacherMotherHobbyPvtRepository, SlaveCustomTeacherChildHobbyPvtRepository, SlaveCustomHobbyStudentSiblingPvtRepository,
        SlaveCustomTeacherHobbyPvtRepository, SlaveCustomHobbyStudentPvtRepository, SlaveCustomHobbyStudentGuardianPvtRepository,
        SlaveCustomTeacherSpouseHobbyPvtRepository, SlaveCustomStudentSpouseHobbyPvtRepository, SlaveCustomStudentChildHobbyPvtRepository,
        SlaveCustomTeacherGuardianHobbyPvtRepository {

    Flux<SlaveHobbyEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveHobbyEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveHobbyEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveHobbyEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveHobbyEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

//    //query used in pvt mapping handler
//    @Query("SELECT count(*) FROM hobbies\n" +
//            "WHERE hobbies.uuid NOT IN(\n" +
//            "SELECT hobbies.uuid FROM hobbies\n" +
//            "LEFT JOIN std_hobbies_pvt\n" +
//            "ON std_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
//            "WHERE std_hobbies_pvt.std_uuid = :studentUUID\n" +
//            "AND std_hobbies_pvt.deleted_at IS NULL\n" +
//            "AND hobbies.deleted_at IS NULL )\n" +
//            "AND hobbies.deleted_at IS NULL " +
//            "AND (hobbies.name ILIKE concat('%',:name,'%') ) \n")
//    Mono<Long> countExistingRecords(UUID studentUUID, String name);

    //query used in pvt student Guardian  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_grd_hobbies_pvt\n" +
            "ON std_grd_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_grd_hobbies_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "AND std_grd_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingGuardianRecords(UUID stdGuardianUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_grd_hobbies_pvt\n" +
            "ON std_grd_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_grd_hobbies_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "AND std_grd_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentGuardianHobbiesRecordsWithStatus(UUID stdGuardianUUID, String name, String description, Boolean status);


    /**
     * Count existing records that are not Mapped against Student Mother
     **/
    //query used in pvt student Mother  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_mth_hobbies_pvt\n" +
            "ON std_mth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_mth_hobbies_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') or" +
            " hobbies.description ILIKE concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentMotherHobbiesRecords(UUID studentMotherUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_mth_hobbies_pvt\n" +
            "ON std_mth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_mth_hobbies_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "AND std_mth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentMotherHobbiesRecordsWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Student Mother
     **/
    // query used for count of mapped hobbies records for given student mother profile
    @Query("select count(*) from hobbies\n" +
            "left join std_mth_hobbies_pvt\n" +
            "on hobbies.uuid = std_mth_hobbies_pvt.hobby_uuid\n" +
            "where std_mth_hobbies_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_mth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentMotherHobbies(UUID studentMotherUUID, String name, String description);

    // query used for count of mapped hobbies records for given student mother profile
    @Query("select count(*) from hobbies\n" +
            "left join std_mth_hobbies_pvt\n" +
            "on hobbies.uuid = std_mth_hobbies_pvt.hobby_uuid\n" +
            "where std_mth_hobbies_pvt.std_mother_uuid = :studentMotherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_mth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentMotherHobbiesWithStatus(UUID studentMotherUUID, String name, String description, Boolean status);

    /**
     * Count Hobby Records that are not mapped against Teacher Mother Yet with Status and without Status Filter
     */
    //query used in pvt Teacher Mother Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_mth_hobbies_pvt\n" +
            "ON teacher_mth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_mth_hobbies_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherMotherHobbiesRecords(UUID teacherMotherUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_mth_hobbies_pvt\n" +
            "ON teacher_mth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_mth_hobbies_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "AND teacher_mth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherMotherHobbiesRecordsWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Sibling Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_sibling_hobbies_pvt\n" +
            "ON teacher_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_sibling_hobbies_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "OR hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingHobbiesRecords(UUID teacherSiblingUUID, String name, String description);

    //query used in pvt Teacher Sibling Hobbies Pvt With Status
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_sibling_hobbies_pvt\n" +
            "ON teacher_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_sibling_hobbies_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "AND teacher_sibling_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "OR hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingTeacherSiblingHobbiesRecordsWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);

    // query used for count of mapped hobbies records for given teacher sibling
    @Query("select count(*) from hobbies\n" +
            "left join teacher_sibling_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_sibling_hobbies_pvt.hobby_uuid\n" +
            "where teacher_sibling_hobbies_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_sibling_hobbies_pvt.deleted_at is null\n" +
            "and (hobbies.name ilike concat('%',:name,'%') " +
            "or hobbies.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedTeacherSiblingHobbies(UUID teacherSiblingUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher sibling with status
    @Query("select count(*) from hobbies\n" +
            "left join teacher_sibling_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_sibling_hobbies_pvt.hobby_uuid\n" +
            "where teacher_sibling_hobbies_pvt.teacher_sibling_uuid = :teacherSiblingUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_sibling_hobbies_pvt.deleted_at is null\n" +
            "and (hobbies.name ilike concat('%',:name,'%') " +
            "or hobbies.description ilike concat('%',:description,'%')) \n")
    Mono<Long> countMappedTeacherSiblingHobbiesWithStatus(UUID teacherSiblingUUID, String name, String description, Boolean status);


    //query used in pvt Teacher Child Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_child_hobbies_pvt\n" +
            "ON teacher_child_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_child_hobbies_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "OR hobbies.description ILIKE concat('%',:description,'%') )\n")
    Mono<Long> countExistingTeacherChildHobbiesRecords(UUID teacherChildUUID, String name, String description);

    //query used in pvt Teacher Child Hobbies Pvt With Status
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_child_hobbies_pvt\n" +
            "ON teacher_child_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_child_hobbies_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "AND teacher_child_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "OR hobbies.description ILIKE concat('%',:description,'%') )\n")
    Mono<Long> countExistingTeacherChildHobbiesRecordsWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    // query used for count of mapped hobbies records for given teacher child
    @Query("select count(*) from hobbies\n" +
            "left join teacher_child_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_child_hobbies_pvt.hobby_uuid\n" +
            "where teacher_child_hobbies_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_child_hobbies_pvt.deleted_at is null\n" +
            "and (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%'))")
    Mono<Long> countMappedTeacherChildHobbies(UUID teacherChildUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher child with status
    @Query("select count(*) from hobbies\n" +
            "left join teacher_child_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_child_hobbies_pvt.hobby_uuid\n" +
            "where teacher_child_hobbies_pvt.teacher_child_uuid = :teacherChildUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_child_hobbies_pvt.deleted_at is null\n" +
            "and (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%'))")
    Mono<Long> countMappedTeacherChildHobbiesWithStatus(UUID teacherChildUUID, String name, String description, Boolean status);

    /***Count Existing Hobbies  that are not mapped against Student Sibling  yet**/
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_sibling_hobbies_pvt\n" +
            "ON std_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_sibling_hobbies_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "AND std_sibling_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%')" +
            " or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStdSiblingHobbiesRecords(UUID stdSiblingUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_sibling_hobbies_pvt\n" +
            "ON std_sibling_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_sibling_hobbies_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "AND std_sibling_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStdSiblingHobbiesRecordsWithStatus(UUID stdSiblingUUID, String name, String description, Boolean status);

    /**
     * Count Hobbies that are mapped for Given Student Sibling  with and without Status Filter
     **/
    @Query("select count(*) from hobbies\n" +
            "left join std_sibling_hobbies_pvt\n" +
            "on hobbies.uuid = std_sibling_hobbies_pvt.hobby_uuid\n" +
            "where std_sibling_hobbies_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_sibling_hobbies_pvt.deleted_at is null\n" +
            "and hobbies.name ILIKE concat('%',:name,'%') or" +
            " hobbies.description ILIKE concat('%',:description,'%')")
    Mono<Long> countMappedStudentSiblingHobbies(UUID stdSiblingUUID, String name, String description);

    // query used for count of mapped hobbies records for given Student sibling profile
    @Query("select count(*) from hobbies\n" +
            "left join std_sibling_hobbies_pvt\n" +
            "on hobbies.uuid = std_sibling_hobbies_pvt.hobby_uuid\n" +
            "where std_sibling_hobbies_pvt.std_sibling_uuid = :stdSiblingUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_sibling_hobbies_pvt.deleted_at is null\n" +
            "and hobbies.name ILIKE concat('%',:name,'%') or" +
            " hobbies.description ILIKE concat('%',:description,'%')")
    Mono<Long> countMappedStudentSiblingHobbiesWithStatus(UUID stdSiblingUUID, String name, String description, Boolean status);

    //query used in pvt Teacher Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_hobbies_pvt\n" +
            "ON teacher_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_hobbies_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherHobbiesRecords(UUID teacherUUID, String name, String description);

    /**
     * This Function is used to Count records that are not mapped based on Status Filter Check against Teacher
     **/
    //query used in pvt Teacher  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_hobbies_pvt\n" +
            "ON teacher_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_hobbies_pvt.teacher_uuid = :teacherUUID\n" +
            "AND teacher_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status =:status \n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherHobbiesRecordsWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    /**
     * This Function is used to Count records that are not mapped based on Status Filter Check against Teacher Spouse
     **/
    //query used in pvt Teacher Spouse Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_spouse_hobbies_pvt\n" +
            "ON teacher_spouse_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_spouse_hobbies_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "AND teacher_spouse_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherSpouseHobbiesRecords(UUID teacherSpouseUUID, String name, String description);

    //query used in pvt Teacher Spouse  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_spouse_hobbies_pvt\n" +
            "ON teacher_spouse_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_spouse_hobbies_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "AND teacher_spouse_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status =:status \n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherSpouseHobbiesRecordsWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Teacher
     **/
    // query used for count of mapped hobbies records for given teacher
    @Query("select count(*) from hobbies\n" +
            "left join teacher_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_hobbies_pvt.hobby_uuid\n" +
            "where teacher_hobbies_pvt.teacher_uuid = :teacherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherHobbies(UUID teacherUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher
    @Query("select count(*) from hobbies\n" +
            "left join teacher_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_hobbies_pvt.hobby_uuid\n" +
            "where teacher_hobbies_pvt.teacher_uuid = :teacherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherHobbiesWithStatus(UUID teacherUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Teacher Spouse
     **/
    // query used for count of mapped hobbies records for given teacher spouse
    @Query("select count(*) from hobbies\n" +
            "left join teacher_spouse_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_spouse_hobbies_pvt.hobby_uuid\n" +
            "where teacher_spouse_hobbies_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_spouse_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherSpouseHobbies(UUID teacherSpouseUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher spouse with status
    @Query("select count(*) from hobbies\n" +
            "left join teacher_spouse_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_spouse_hobbies_pvt.hobby_uuid\n" +
            "where teacher_spouse_hobbies_pvt.teacher_spouse_uuid = :teacherSpouseUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_spouse_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherSpouseHobbiesWithStatus(UUID teacherSpouseUUID, String name, String description, Boolean status);

    /**
     * Count Existing Teacher Father Hobbies Records with or without status filter
     **/
    //query used in pvt teacher Father Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_fth_hobbies_pvt\n" +
            "ON teacher_fth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_fth_hobbies_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherHobbiesRecords(UUID teacherFatherUUID, String name, String description);

    //query used in pvt teacher Father Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_fth_hobbies_pvt\n" +
            "ON teacher_fth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_fth_hobbies_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "AND teacher_fth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status \n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherFatherHobbiesRecordsWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Teacher Father
     **/
    // query used for count of mapped hobbies records for given teacher
    @Query("select count(*) from hobbies\n" +
            "left join teacher_fth_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_fth_hobbies_pvt.hobby_uuid\n" +
            "where teacher_fth_hobbies_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_fth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherFatherHobbies(UUID teacherFatherUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher
    @Query("select count(*) from hobbies\n" +
            "left join teacher_fth_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_fth_hobbies_pvt.hobby_uuid\n" +
            "where teacher_fth_hobbies_pvt.teacher_father_uuid = :teacherFatherUUID\n" +
            "and hobbies.deleted_at is null \n" +
            "and hobbies.status = :status " +
            "and teacher_fth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherFatherHobbiesWithStatus(UUID teacherFatherUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Teacher
     **/
    // query used for count of mapped hobbies records for given teacher mother
    @Query("select count(*) from hobbies\n" +
            "left join teacher_mth_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_mth_hobbies_pvt.hobby_uuid\n" +
            "where teacher_mth_hobbies_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_mth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherMotherHobbies(UUID teacherMotherUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher mother with status
    @Query("select count(*) from hobbies\n" +
            "left join teacher_mth_hobbies_pvt \n" +
            "on hobbies.uuid = teacher_mth_hobbies_pvt.hobby_uuid\n" +
            "where teacher_mth_hobbies_pvt.teacher_mother_uuid = :teacherMotherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_mth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherMotherHobbiesWithStatus(UUID teacherMotherUUID, String name, String description, Boolean status);


    /**
     * Count Hobby Records that are not mapped against Student profile Yet with Status and without Status Filter
     */
    //query used in pvt Student  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_hobbies_pvt\n" +
            "ON std_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_hobbies_pvt.student_uuid = :studentUUID\n" +
            "AND std_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentHobbiesRecords(UUID studentUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_hobbies_pvt\n" +
            "ON std_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_hobbies_pvt.student_uuid = :studentUUID\n" +
            "AND std_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentHobbiesRecordsWithStatus(UUID studentUUID, String name, String description, Boolean status);

    // query used for count of mapped hobbies records for given student profile
    @Query("select count(*) from hobbies\n" +
            "left join std_hobbies_pvt\n" +
            "on hobbies.uuid = std_hobbies_pvt.hobby_uuid\n" +
            "where std_hobbies_pvt.student_uuid = :studentUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentHobbies(UUID studentUUID, String name, String description);

    // query used for count of mapped hobbies records for given student profile
    @Query("select count(*) from hobbies\n" +
            "left join std_hobbies_pvt \n" +
            "on hobbies.uuid = std_hobbies_pvt.hobby_uuid\n" +
            "where std_hobbies_pvt.student_uuid = :studentUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentHobbiesWithStatus(UUID studentUUID, String name, String description, Boolean status);

    /**
     * Count Hobby Records that are not mapped against Student Father profile Yet with Status and without Status Filter
     */
    //query used in pvt Student father  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_fth_hobbies_pvt\n" +
            "ON std_fth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_fth_hobbies_pvt.std_father_uuid = :studentFatherUUID \n" +
            "AND std_fth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentFatherHobbiesRecords(UUID studentFatherUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_fth_hobbies_pvt\n" +
            "ON std_fth_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_fth_hobbies_pvt.std_father_uuid = :studentFatherUUID\n" +
            "AND std_fth_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentFatherHobbiesRecordsWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    /**
     * Count Hobby Records that are mapped against Student Father profiles with Status and without Status Filter
     */
    // query used for count of mapped hobbies records for given student father profile
    @Query("select count(*) from hobbies\n" +
            "left join std_fth_hobbies_pvt\n" +
            "on hobbies.uuid = std_fth_hobbies_pvt.hobby_uuid\n" +
            "where std_fth_hobbies_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_fth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentFatherHobbies(UUID studentFatherUUID, String name, String description);

    // query used for count of mapped hobbies records for given student father profile
    @Query("select count(*) from hobbies\n" +
            "left join std_fth_hobbies_pvt \n" +
            "on hobbies.uuid = std_fth_hobbies_pvt.hobby_uuid\n" +
            "where std_fth_hobbies_pvt.std_father_uuid = :studentFatherUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_fth_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentFatherHobbiesWithStatus(UUID studentFatherUUID, String name, String description, Boolean status);

    /**
     * Count Hobbies that are mapped for Given Student Guardian with and without Status Filter
     **/
    @Query("select count(*) from hobbies\n" +
            "left join std_grd_hobbies_pvt\n" +
            "on hobbies.uuid = std_grd_hobbies_pvt.hobby_uuid\n" +
            "where std_grd_hobbies_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_grd_hobbies_pvt.deleted_at is null\n" +
            "and hobbies.name ILIKE concat('%',:name,'%') or" +
            " hobbies.description ILIKE concat('%',:description,'%')")
    Mono<Long> countMappedStudentGuardianHobbies(UUID stdGuardianUUID, String name, String description);

    // query used for count of mapped hobbies records for given Student Guardian profile
    @Query("select count(*) from hobbies\n" +
            "left join std_grd_hobbies_pvt\n" +
            "on hobbies.uuid = std_grd_hobbies_pvt.hobby_uuid\n" +
            "where std_grd_hobbies_pvt.std_guardian_uuid = :stdGuardianUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_grd_hobbies_pvt.deleted_at is null\n" +
            "and hobbies.name ILIKE concat('%',:name,'%') or" +
            " hobbies.description ILIKE concat('%',:description,'%')")
    Mono<Long> countMappedStudentGuardianHobbiesWithStatus(UUID stdGuardianUUID, String name, String description, Boolean status);


    //query used in pvt Student Spouse Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_spouse_hobbies_pvt\n" +
            "ON std_spouse_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_spouse_hobbies_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentSpouseHobbiesRecords(UUID stdSpouseUUID, String name, String description);

    /**
     * This Function is used to Count records that are not mapped based on Status Filter Check against Student Spouse
     **/
    //query used in pvt Student Spouse Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_spouse_hobbies_pvt\n" +
            "ON std_spouse_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_spouse_hobbies_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "AND std_spouse_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status =:status \n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingStudentSpouseHobbiesRecordsWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given StudentSpouse
     **/
    // query used for count of mapped hobbies records for given teacher spouse
    @Query("select count(*) from hobbies\n" +
            "left join std_spouse_hobbies_pvt\n" +
            "on hobbies.uuid = std_spouse_hobbies_pvt.hobby_uuid\n" +
            "where std_spouse_hobbies_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_spouse_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentSpouseHobbies(UUID stdSpouseUUID, String name, String description);

    // query used for count of mapped hobbies records for given std spouse
    @Query("select count(*) from hobbies\n" +
            "left join std_spouse_hobbies_pvt\n" +
            "on hobbies.uuid = std_spouse_hobbies_pvt.hobby_uuid\n" +
            "where std_spouse_hobbies_pvt.std_spouse_uuid = :stdSpouseUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_spouse_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentSpouseHobbiesWithStatus(UUID stdSpouseUUID, String name, String description, Boolean status);

    /**
     * Count Hobby Records that are not mapped against Student Child profile Yet with Status and without Status Filter
     */
    //query used in pvt Student father  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_child_hobbies_pvt\n" +
            "ON std_child_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_child_hobbies_pvt.std_child_uuid = :studentChildUUID \n" +
            "AND std_child_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentChildHobbiesRecords(UUID studentChildUUID, String name, String description);

    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN std_child_hobbies_pvt\n" +
            "ON std_child_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE std_child_hobbies_pvt.std_child_uuid = :studentChildUUID\n" +
            "AND std_child_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status = :status " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE concat('%',:description,'%')) \n")
    Mono<Long> countExistingStudentChildHobbiesRecordsWithStatus(UUID studentChildUUID, String name, String description, Boolean status);

    /**
     * Count Hobby Records that are mapped against Student Child  with Status and without Status Filter
     */
    // query used for count of mapped hobbies records for given student child profile
    @Query("select count(*) from hobbies\n" +
            "left join std_child_hobbies_pvt\n" +
            "on hobbies.uuid = std_child_hobbies_pvt.hobby_uuid\n" +
            "where std_child_hobbies_pvt.std_child_uuid = :studentChildUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and std_child_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentChildHobbies(UUID studentChildUUID, String name, String description);

    // query used for count of mapped hobbies records for given student child
    @Query("select count(*) from hobbies\n" +
            "left join std_child_hobbies_pvt \n" +
            "on hobbies.uuid = std_child_hobbies_pvt.hobby_uuid\n" +
            "where std_child_hobbies_pvt.std_child_uuid = :studentChildUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and std_child_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedStudentChildHobbiesWithStatus(UUID studentChildUUID, String name, String description, Boolean status);

    /**
     * This Function is used to Count records that are not mapped based on Status Filter Check against Teacher Guardian
     **/
    //query used in pvt Teacher Guardian Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_grd_hobbies_pvt\n" +
            "ON teacher_grd_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_grd_hobbies_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherGuardianHobbiesRecords(UUID teacherGuardianUUID, String name, String description);

    //query used in pvt Teacher Guardian  Hobbies Pvt
    @Query("SELECT count(*) FROM hobbies\n" +
            "WHERE hobbies.uuid NOT IN(\n" +
            "SELECT hobbies.uuid FROM hobbies\n" +
            "LEFT JOIN teacher_grd_hobbies_pvt\n" +
            "ON teacher_grd_hobbies_pvt.hobby_uuid = hobbies.uuid \n" +
            "WHERE teacher_grd_hobbies_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "AND teacher_grd_hobbies_pvt.deleted_at IS NULL\n" +
            "AND hobbies.deleted_at IS NULL )\n" +
            "AND hobbies.deleted_at IS NULL " +
            "AND hobbies.status =:status \n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countExistingTeacherGuardianHobbiesRecordsWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);

    /**
     * Count the Number of Hobbies Mapped for a Given Teacher Guardian
     **/
    // query used for count of mapped hobbies records for given teacher guardian
    @Query("select count(*) from hobbies\n" +
            "left join teacher_grd_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_grd_hobbies_pvt.hobby_uuid\n" +
            "where teacher_grd_hobbies_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and teacher_grd_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherGuardianHobbies(UUID teacherGuardianUUID, String name, String description);

    // query used for count of mapped hobbies records for given teacher guardian with status
    @Query("select count(*) from hobbies\n" +
            "left join teacher_grd_hobbies_pvt\n" +
            "on hobbies.uuid = teacher_grd_hobbies_pvt.hobby_uuid\n" +
            "where teacher_grd_hobbies_pvt.teacher_guardian_uuid = :teacherGuardianUUID\n" +
            "and hobbies.deleted_at is null\n" +
            "and hobbies.status = :status " +
            "and teacher_grd_hobbies_pvt.deleted_at is null\n" +
            "AND (hobbies.name ILIKE concat('%',:name,'%') " +
            "or hobbies.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedTeacherGuardianHobbiesWithStatus(UUID teacherGuardianUUID, String name, String description, Boolean status);

}

