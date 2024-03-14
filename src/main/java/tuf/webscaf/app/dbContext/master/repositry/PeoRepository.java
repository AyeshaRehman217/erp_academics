package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.PeoEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface PeoRepository extends ReactiveCrudRepository<PeoEntity, Long> {
    Mono<PeoEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<PeoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<PeoEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(String code, UUID departmentUUID);

    Mono<PeoEntity> findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(String code, UUID departmentUUID, UUID uuid);

    Flux<PeoEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
