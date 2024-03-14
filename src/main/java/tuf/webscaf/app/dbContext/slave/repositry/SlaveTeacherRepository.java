package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherRepository extends ReactiveCrudRepository<SlaveTeacherEntity, Long>, SlaveCustomTeacherRepository {
    Flux<SlaveTeacherEntity> findAllByEmployeeCodeContainingIgnoreCaseAndDeletedAtIsNull(String employeeCode, Pageable pageable);

    Flux<SlaveTeacherEntity> findAllByEmployeeCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String employeeCode, Boolean status);

    Mono<Long> countByEmployeeCodeContainingIgnoreCaseAndDeletedAtIsNull(String employeeCode);

    Mono<Long> countByEmployeeCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String employeeCode, Boolean status);

    Mono<SlaveTeacherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Count All teacher records in index with and without status filter
     **/

    @Query("select count(*)  \n" +
            "from teachers \n" +
            "left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
            " where teachers.deleted_at IS NULL\n" +
            " and teacher_profiles.deleted_at IS NULL\n" +
            " AND (teachers.employee_code ILIKE concat('%',:employeeCode,'%') \n" +
            " or concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countIndexRecordsWithoutStatusFilter(String employeeCode, String key);

    @Query("select count(*) \n" +
            "from teachers \n" +
            "left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
            " where teachers.deleted_at IS NULL \n" +
            " and teachers.status =:status " +
            " and teacher_profiles.deleted_at IS NULL\n" +
            " AND (teachers.employee_code ILIKE concat('%',:employeeCode,'%') \n" +
            " or concat_ws('|',teachers.employee_code,teacher_profiles.first_name,teacher_profiles.last_name) ILIKE concat('%',:key,'%')) ")
    Mono<Long> countIndexRecordsWithStatusFilter(Boolean status, String employeeCode, String key);

    /**
     * Count All teacher ,Teacher Profile and Teacher Contact no records in index with and without status filter (Facade Implementation)
     **/

    @Query("select distinct count(*) \n" +
            " from teachers \n" +
            " left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
            " where teachers.deleted_at is null \n" +
            " and teacher_profiles.deleted_at is null \n" +
            " AND (teacher_profiles.first_name ILIKE concat('%',:firstName,'%') " +
            " or teacher_profiles.last_name ILIKE concat('%',:lastName,'%') " +
            " or teachers.employee_code ILIKE concat('%',:employeeCode,'%') " +
            " or teacher_profiles.nic ILIKE concat('%',:nic,'%')" +
            " or teacher_profiles.email ILIKE concat('%',:email,'%') " +
            "or teacher_profiles.tel ILIKE concat('%',:telephoneNo,'%'))")
    Mono<Long> countIndexTeacherTeacherProfileContactNoWithoutStatusFilter(String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic);

    @Query("select distinct count(*) \n" +
            " from teachers \n" +
            " left join teacher_profiles on teachers.uuid = teacher_profiles.teacher_uuid \n" +
            " where teachers.deleted_at is null \n" +
            " and teacher_profiles.deleted_at is null \n" +
            " and teachers.status = :status \n" +
            " AND (teacher_profiles.first_name ILIKE concat('%',:firstName,'%') " +
            " or teacher_profiles.last_name ILIKE concat('%',:lastName,'%') " +
            " or teachers.employee_code ILIKE concat('%',:employeeCode,'%') " +
            " or teacher_profiles.nic ILIKE concat('%',:nic,'%')" +
            " or teacher_profiles.email ILIKE concat('%',:email,'%') " +
            "or teacher_profiles.tel ILIKE concat('%',:telephoneNo,'%'))")
    Mono<Long> countIndexTeacherTeacherProfileContactNoWithStatusFilter(Boolean status, String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic);

}
