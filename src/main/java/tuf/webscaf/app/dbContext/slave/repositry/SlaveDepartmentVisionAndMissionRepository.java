package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentVisionAndMissionDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentVisionAndMissionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomDepartmentVisionAndMissionRepository;

import java.util.UUID;

@Repository
public interface SlaveDepartmentVisionAndMissionRepository extends ReactiveCrudRepository<SlaveDepartmentVisionAndMissionEntity, Long>, SlaveCustomDepartmentVisionAndMissionRepository {
    Mono<SlaveDepartmentVisionAndMissionEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query("select department_vision_and_missions.*, concat(departments.name,'|',department_vision_and_missions.vision) as name\n" +
            "from department_vision_and_missions \n" +
            "join departments on departments.uuid = department_vision_and_missions.department_uuid \n" +
            "where department_vision_and_missions.uuid = :uuid " +
            "and departments.deleted_at is null " +
            "and department_vision_and_missions.deleted_at is null")
    Mono<SlaveDepartmentVisionAndMissionDto> showDepartmentVisionAndMissionAgainstUUID(UUID uuid);

    Flux<SlaveDepartmentVisionAndMissionEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description, String vision, String mission);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description, String vision, String mission);

    Flux<SlaveDepartmentVisionAndMissionEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2, String vision, Boolean status3, String mission, Boolean status4);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2, String vision, Boolean status3, String mission, Boolean status4);


    @Query("select count(*)\n" +
            "from department_vision_and_missions \n" +
            "join departments  on departments.uuid = department_vision_and_missions.department_uuid \n" +
            "and concat(departments.name,'|',department_vision_and_missions.vision) ILIKE concat('%',:vision,'%') " +
            " and concat(departments.name,'|',department_vision_and_missions.mission) ILIKE concat('%',:mission,'%') " +
            "and departments.deleted_at is null " +
            "and department_vision_and_missions.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNull(String vision, String mission);

    @Query("select count(*)\n" +
            "from department_vision_and_missions \n" +
            "join departments  on departments.uuid = department_vision_and_missions.department_uuid \n" +
            "and concat(departments.name,'|',department_vision_and_missions.vision) ILIKE concat('%',:vision,'%') " +
            "and concat(departments.name,'|',department_vision_and_missions.mission) ILIKE concat('%',:mission,'%') " +
            "and department_vision_and_missions.status= :status " +
            "and departments.deleted_at is null " +
            "and department_vision_and_missions.deleted_at is null")
    Mono<Long> countAllByDeletedAtIsNullAndStatus(String vision, String mission, Boolean status);
}
