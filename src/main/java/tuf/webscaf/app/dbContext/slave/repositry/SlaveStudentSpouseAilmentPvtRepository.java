package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseAilmentPvtRepository extends ReactiveCrudRepository<SlaveStudentSpouseAilmentPvtEntity, Long> {

    Mono<SlaveStudentSpouseAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
