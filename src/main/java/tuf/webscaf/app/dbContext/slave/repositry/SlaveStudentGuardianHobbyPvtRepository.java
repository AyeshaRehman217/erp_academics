package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianHobbyPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomHobbyStudentGuardianPvtRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentGuardianHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentGuardianHobbyPvtEntity, Long>, SlaveCustomHobbyStudentGuardianPvtRepository {
    Mono<SlaveStudentGuardianHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentGuardianHobbyPvtEntity> findAllByStudentGuardianUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID stdGuardianUUID, List<UUID> hobbyUUID);

    Flux<SlaveStudentGuardianHobbyPvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianUUID);
}
