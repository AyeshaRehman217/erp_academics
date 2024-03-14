package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingAilmentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomAilmentStudentSiblingPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentSiblingAilmentPvtEntity, Long>, SlaveCustomAilmentStudentSiblingPvtRepository {
    Mono<SlaveStudentSiblingAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
