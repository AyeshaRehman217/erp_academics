package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianAilmentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentGuardianPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentGuardianAilmentPvtEntity, Long>, SlaveCustomAilmentStudentGuardianPvtRepository {
    Mono<SlaveStudentGuardianAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentGuardianAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
