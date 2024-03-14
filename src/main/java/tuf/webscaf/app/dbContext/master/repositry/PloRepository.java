package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.PloEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface PloRepository extends ReactiveCrudRepository<PloEntity, Long> {
    Mono<PloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<PloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<PloEntity> findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String name, UUID departmentUUID);

    Mono<PloEntity> findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID departmentUUID, UUID uuid);

    Mono<PloEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String code, UUID departmentUUID);

    Mono<PloEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String code, UUID departmentUUID, UUID uuid);

    Flux<PloEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
