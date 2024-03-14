package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DepartmentVisionAndMissionEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentVisionAndMissionRepository extends ReactiveCrudRepository<DepartmentVisionAndMissionEntity, Long> {
    Mono<DepartmentVisionAndMissionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DepartmentVisionAndMissionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<DepartmentVisionAndMissionEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);

    Mono<DepartmentVisionAndMissionEntity> findFirstByDepartmentUUIDAndDeletedAtIsNull(UUID DepartmentUUID);

    Mono<DepartmentVisionAndMissionEntity> findFirstByVisionAndDepartmentUUIDAndDeletedAtIsNull(String name, UUID DepartmentUUID);

    Mono<DepartmentVisionAndMissionEntity> findFirstByMissionAndDepartmentUUIDAndDeletedAtIsNull(String name,UUID DepartmentUUID);

    Mono<DepartmentVisionAndMissionEntity> findFirstByVisionAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID DepartmentUUID,UUID uuid);

    Mono<DepartmentVisionAndMissionEntity> findFirstByMissionAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String name,UUID DepartmentUUID,UUID uuid);

    Mono<DepartmentVisionAndMissionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<DepartmentVisionAndMissionEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
