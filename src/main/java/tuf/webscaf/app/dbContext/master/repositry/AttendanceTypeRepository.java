package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AttendanceTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceTypeRepository extends ReactiveCrudRepository<AttendanceTypeEntity, Long> {
    Mono<AttendanceTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AttendanceTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<AttendanceTypeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Flux<AttendanceTypeEntity> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    Mono<AttendanceTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AttendanceTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
