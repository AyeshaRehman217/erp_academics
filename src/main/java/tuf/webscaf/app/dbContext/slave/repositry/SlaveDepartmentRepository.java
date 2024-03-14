package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentRepository;

import java.util.UUID;

@Repository
public interface SlaveDepartmentRepository extends ReactiveCrudRepository<SlaveDepartmentEntity, Long>, SlaveCustomDepartmentRepository {
    Mono<SlaveDepartmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveDepartmentEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String shortName);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String shortName);

    Flux<SlaveDepartmentEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2, String shortName, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2, String shortName, Boolean status3);

    //fetch Records with Faculty Filter
    Flux<SlaveDepartmentEntity> findAllByNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID facultyUUID1, String description, UUID facultyUUID2, String shortName, UUID facultyUUID3);

    Mono<Long> countByNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNull(String name, UUID facultyUUID1, String description, UUID facultyUUID2, String shortName, UUID facultyUUID3);

    //fetch Records with Faculty Filter and Status Filter
    Flux<SlaveDepartmentEntity> findAllByNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID facultyUUID1, Boolean status, String description, UUID facultyUUID2, Boolean status2, String shortName, UUID facultyUUID3, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNull(String name, UUID facultyUUID1, Boolean status, String description, UUID facultyUUID2, Boolean status2, String shortName, UUID facultyUUID3, Boolean status3);

    Mono<SlaveDepartmentEntity> findByIdAndDeletedAtIsNull(Long id);

    //    used in seeder
    Mono<SlaveDepartmentEntity> findByNameAndDeletedAtIsNull(String name);

    /**
     * Count Department Rank Catalogues Records that are mapped against Department Rank with Status and without Status Filter
     */
    // query used for count of mapped department_rank_catalogues records for given student child profile
    @Query("SELECT count(*)" +
            " FROM departments\n" +
            "left join department_ranks \n" +
            "on departments.uuid = department_ranks.department_uuid\n" +
            "where department_ranks.uuid =   :departmentRankUUID\n" +
            " and departments.deleted_at is null\n" +
            "and department_ranks.deleted_at is null\n" +
            "AND (departments.name ILIKE concat('%',:name,'%') " +
            "or departments.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedDepartment(UUID departmentRanUUID, String name, String description);

    // query used for count of mapped department rank catalogues records for given Department Rank
    @Query("SELECT count(*)" +
            " FROM departments\n" +
            "left join department_ranks \n" +
            "on departments.uuid = department_ranks.department_uuid\n" +
            "where department_ranks.uuid =   :departmentRankUUID\n" +
            " and departments.deleted_at is null\n" +
            "and department_ranks.deleted_at is null\n" +
            "AND (departments.name ILIKE concat('%',:name,'%') " +
            "AND departments.status = :status " +
            "or departments.description ILIKE  concat('%',:description,'%') ) \n")
    Mono<Long> countMappedDepartmentWithStatus(UUID departmentRanUUID, String name, String description, Boolean status);
}
