package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlavePloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCloPloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPloRepository;

import java.util.UUID;

@Repository
public interface SlavePloRepository extends ReactiveCrudRepository<SlavePloEntity, Long>, SlaveCustomPloRepository {

    Mono<SlavePloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlavePloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlavePloEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String code, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String code, String description);

    Flux<SlavePloEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String code, Boolean status2, String description, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String code, Boolean status2, String description, Boolean status3);

    Flux<SlavePloEntity> findAllByNameContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID departmentUUID, String code, UUID departmentUUID2, String description, UUID departmentUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String name, UUID departmentUUID, String code, UUID departmentUUID2, String description, UUID departmentUUID3);

    Flux<SlavePloEntity> findAllByNameContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID departmentUUID, Boolean status, String code, UUID departmentUUID2, Boolean status2, String description, UUID departmentUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDepartmentUUIDAndStatusAndDeletedAtIsNull(String name, UUID departmentUUID, Boolean status, String code, UUID departmentUUID2, Boolean status2, String description, UUID departmentUUID3, Boolean status3);



    /**
     * Count All Plo Records With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from plos \n" +
            " where plos.deleted_at is null\n" +
            " and plos.status= :status\n" +
            " and \n" +
            " ( concat_ws('|',plos.name,plos.code,plos.description) ILIKE concat('%',:title,'%') " +
            " OR plos.name ILIKE concat('%',:name,'%') \n" +
            " OR plos.code ILIKE concat('%',:code,'%') \n" +
            " OR plos.description ILIKE concat('%',:description,'%') )" )
    Mono<Long> countPloAgainstStatus(Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from plos \n" +
            " where plos.deleted_at is null \n" +
            " and \n" +
            " ( concat_ws('|',plos.name,plos.code,plos.description) ILIKE concat('%',:title,'%') " +
            " OR plos.name ILIKE concat('%',:name,'%') \n" +
            " OR plos.code ILIKE concat('%',:code,'%') \n" +
            " OR plos.description ILIKE concat('%',:description,'%') )" )
    Mono<Long> countPloWithoutStatus(String title, String name, String code, String description);


    /**
     * Count All Plo Records Against department & With & Without Status
     **/
    @Query("select count(*)  \n" +
            " from plos \n" +
            " where plos.deleted_at is null\n" +
            " and plos.department_uuid= :departmentUUID\n" +
            " and plos.status= :status\n" +
            " and \n" +
            " ( concat_ws('|',plos.name,plos.code,plos.description) ILIKE concat('%',:title,'%') " +
            " OR plos.name ILIKE concat('%',:name,'%') \n" +
            " OR plos.code ILIKE concat('%',:code,'%') \n" +
            " OR plos.description ILIKE concat('%',:description,'%') )" )
    Mono<Long> countPloAgainstDepartmentAndStatus(UUID departmentUUID, Boolean status, String title, String name, String code, String description);

    @Query("select count(*)  \n" +
            " from plos \n" +
            " where plos.deleted_at is null\n" +
            " and plos.department_uuid= :departmentUUID\n" +
            " and \n" +
            " ( concat_ws('|',plos.name,plos.code,plos.description) ILIKE concat('%',:title,'%') " +
            " OR plos.name ILIKE concat('%',:name,'%') \n" +
            " OR plos.code ILIKE concat('%',:code,'%') \n" +
            " OR plos.description ILIKE concat('%',:description,'%') )" )
    Mono<Long> countPloAgainstDepartment(UUID departmentUUID, String title, String name, String code, String description);
}
