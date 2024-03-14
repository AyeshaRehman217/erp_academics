package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCampusDepartmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCampusDepartmentRepository;

import java.util.UUID;

@Repository
public interface SlaveCampusDepartmentRepository extends ReactiveCrudRepository<SlaveCampusDepartmentEntity, Long>, SlaveCustomCampusDepartmentRepository {

    Mono<SlaveCampusDepartmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCampusDepartmentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Long> countAllByDeletedAtIsNull();


    @Query("select count(*) \n" +
            "from campus_departments \n" +
            "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
            "join departments  on departments.uuid = campus_departments.department_uuid\n" +
            "and concat(campuses.name,'|',departments.name) ILIKE concat('%',:name,'%') " +
            "and campuses.deleted_at is null " +
            "and departments.deleted_at is null " +
            "and campus_departments.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNull(String name);

    @Query("select count(*) \n" +
            "from campus_departments \n" +
            "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
            "join departments  on departments.uuid = campus_departments.department_uuid\n" +
            "and concat(campuses.name,'|',departments.name) ILIKE concat('%',:name,'%') " +
            "and campuses.deleted_at is null " +
            "and departments.deleted_at is null " +
            "and campus_departments.deleted_at is null")
    Mono<Long> countAllRecords(String name);

    @Query("select count(*) \n" +
            "from campus_departments \n" +
            "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
            "join departments  on departments.uuid = campus_departments.department_uuid\n" +
            "and concat(campuses.name,'|',departments.name) ILIKE concat('%',:name,'%') " +
            "and campus_departments.status = :status " +
            "and campus_departments.campus_uuid = :campusUUID " +
            "and campuses.deleted_at is null " +
            "and departments.deleted_at is null " +
            "and campus_departments.deleted_at is null")
    Mono<Long> countAllRecordsWithCampusAndStatus(UUID campusUUID, String name, Boolean status);

    @Query("select count(*) \n" +
            "from campus_departments \n" +
            "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
            "join departments  on departments.uuid = campus_departments.department_uuid\n" +
            "and concat(campuses.name,'|',departments.name) ILIKE concat('%',:name,'%') " +
            "and campus_departments.campus_uuid = :campusUUID " +
            "and campuses.deleted_at is null " +
            "and departments.deleted_at is null " +
            "and campus_departments.deleted_at is null")
    Mono<Long> countAllRecordsWithCampus(UUID campusUUID, String name);

    @Query("select count(*) \n" +
            "from campus_departments \n" +
            "join campuses  on campuses.uuid = campus_departments.campus_uuid \n" +
            "join departments  on departments.uuid = campus_departments.department_uuid\n" +
            "and concat(campuses.name,'|',departments.name) ILIKE concat('%',:name,'%') " +
            "and campus_departments.status = :status " +
            "and campuses.deleted_at is null " +
            "and departments.deleted_at is null " +
            "and campus_departments.deleted_at is null")
    Mono<Long> countAllRecordsWithStatus(String name, Boolean status);

}
