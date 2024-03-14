package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherAilmentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentMotherPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentMotherAilmentPvtEntity, Long>, SlaveCustomAilmentStudentMotherPvtRepository {
    Mono<SlaveStudentMotherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
