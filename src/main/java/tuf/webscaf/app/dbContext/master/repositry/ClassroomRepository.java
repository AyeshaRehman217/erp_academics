package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.ClassroomEntity;

import java.util.UUID;

@Repository
public interface ClassroomRepository extends ReactiveCrudRepository<ClassroomEntity, Long> {
    Mono<ClassroomEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<ClassroomEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<ClassroomEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<ClassroomEntity> findFirstByNameIgnoreCaseAndCampusUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID campusUuid, UUID uuid);

    Mono<ClassroomEntity> findFirstByCodeIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(String code, UUID campusUuid);

    Mono<ClassroomEntity> findFirstByNameIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(String name, UUID campusUuid);

    Mono<ClassroomEntity> findFirstByCodeIgnoreCaseAndCampusUUIDAndDeletedAtIsNullAndUuidIsNot(String code, UUID campusUuid, UUID uuid);

    Flux<ClassroomEntity> findAllByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<ClassroomEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);
}
