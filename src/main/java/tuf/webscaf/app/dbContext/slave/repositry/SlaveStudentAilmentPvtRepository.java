package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentAilmentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentAilmentPvtEntity, Long>, SlaveCustomAilmentStudentPvtRepository {
    Mono<SlaveStudentAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
