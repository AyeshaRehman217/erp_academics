package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveClassroomEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomClassroomRepository;

import java.util.UUID;

@Repository
public interface SlaveClassroomRepository extends ReactiveCrudRepository<SlaveClassroomEntity, Long>, SlaveCustomClassroomRepository {
    Mono<SlaveClassroomEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveClassroomEntity> findAllByNameContainingIgnoreCaseAndCampusUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String name, UUID campusUUID, Boolean status, String description, UUID campusUUID2, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndCampusUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndStatusAndDeletedAtIsNull(String name, UUID campusUUID, Boolean status, String description, UUID campusUUID2, Boolean status2);

    Flux<SlaveClassroomEntity> findAllByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID campusUUID, String description, UUID campusUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(String name, UUID campusUUID, String description, UUID campusUUID2);

    Flux<SlaveClassroomEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Flux<SlaveClassroomEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Mono<SlaveClassroomEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Fetch All classroom records against campus uuid with and without status filter
     **/
    @Query("select count(*) \n" +
            " from classrooms \n" +
            " left join campuses\n" +
            " on classrooms.campus_uuid = campuses.uuid\n" +
            " where \n" +
            " classrooms.deleted_at IS NULL\n" +
            " and  campuses.deleted_at IS NULL\n" +
            " and classrooms.campus_uuid = :campusUUID " +
            " AND (concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) ILIKE concat('%',:key,'%') \n" +
            " or classrooms.code ILIKE concat('%',:code,'%') \n" +
            " or classrooms.name ILIKE concat('%',:name,'%') \n" +
            " or classrooms.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countIndexRecordsCampusWithoutStatusFilter(UUID campusUUID, String code, String key, String name, String description);

    @Query(" select count(*) \n" +
            " from classrooms \n" +
            " left join campuses\n" +
            " on classrooms.campus_uuid = campuses.uuid\n" +
            " where \n" +
            " classrooms.deleted_at IS NULL\n" +
            " and  campuses.deleted_at IS NULL\n" +
            " and classrooms.campus_uuid = :campusUUID " +
            " and classrooms.status = :status " +
            " AND (concat_ws('|',campuses.name,classrooms.code,classrooms.name,classrooms.capacity) ILIKE concat('%',:key,'%') \n" +
            " or classrooms.code ILIKE concat('%',:code,'%') \n" +
            " or classrooms.name ILIKE concat('%',:name,'%') \n" +
            " or classrooms.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countIndexRecordsCampusWithStatusFilter(UUID campusUUID, Boolean status, String code, String key, String name, String description);


    @Query("select count(*) \n" +
            " from classrooms \n" +
            " where classrooms.deleted_at IS NULL\n" +
            " AND (concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) ILIKE concat('%',:key,'%') \n" +
            " or classrooms.code ILIKE concat('%',:code,'%') \n" +
            " or classrooms.name ILIKE concat('%',:name,'%') \n" +
            " or classrooms.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countIndexRecordsWithoutStatusFilter(String code, String key, String name, String description);

    @Query("select count(*) \n" +
            " from classrooms \n" +
            " where classrooms.deleted_at IS NULL\n" +
            " and classrooms.status = :status " +
            " AND (concat_ws('|',classrooms.code,classrooms.name,classrooms.capacity) ILIKE concat('%',:key,'%') \n" +
            " or classrooms.code ILIKE concat('%',:code,'%') \n" +
            " or classrooms.name ILIKE concat('%',:name,'%') \n" +
            " or classrooms.description ILIKE concat('%',:description,'%')) ")
    Mono<Long> countIndexRecordsWithStatusFilter(Boolean status, String code, String key, String name, String description);
}
