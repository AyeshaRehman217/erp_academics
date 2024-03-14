package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentGuardianNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentGuardianNationalityPvtEntity, Long> {
    Mono<SlaveStudentGuardianNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentGuardianNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentGuardianNationalityPvtEntity> findAllByStudentGuardianUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID stdGuardianUUID, List<UUID> nationalityUUID);

    Flux<SlaveStudentGuardianNationalityPvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianUUID);

}
