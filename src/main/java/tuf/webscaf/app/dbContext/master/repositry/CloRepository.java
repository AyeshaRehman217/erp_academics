package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CloEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CloRepository extends ReactiveCrudRepository<CloEntity, Long> {
    Mono<CloEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CloEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CloEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String code, UUID departmentUUID);

    Mono<CloEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String code, UUID departmentUUID, UUID uuid);

    Mono<CloEntity> findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String name, UUID departmentUUID);

    Mono<CloEntity> findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID departmentUUID, UUID uuid);

    Flux<CloEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

}
