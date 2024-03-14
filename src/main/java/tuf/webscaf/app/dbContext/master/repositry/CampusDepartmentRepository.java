package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CampusDepartmentEntity;

import java.util.UUID;

@Repository
public interface CampusDepartmentRepository extends ReactiveCrudRepository<CampusDepartmentEntity, Long> {
    Mono<CampusDepartmentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CampusDepartmentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CampusDepartmentEntity> findFirstByCampusUUIDAndDepartmentUUIDAndDeletedAtIsNull(UUID campusUUID, UUID departmentUUID);

    Mono<CampusDepartmentEntity> findFirstByCampusUUIDAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID campusUUID,UUID departmentUUID, UUID uuid);
}
