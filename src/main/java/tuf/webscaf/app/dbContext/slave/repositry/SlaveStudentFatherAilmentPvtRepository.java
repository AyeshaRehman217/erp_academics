package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherAilmentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentFatherPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentFatherAilmentPvtEntity, Long>, SlaveCustomAilmentStudentFatherPvtRepository {
    Mono<SlaveStudentFatherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
