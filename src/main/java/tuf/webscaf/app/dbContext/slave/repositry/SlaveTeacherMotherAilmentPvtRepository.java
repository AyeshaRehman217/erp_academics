package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherMotherAilmentPvtEntity, Long> {

    Mono<SlaveTeacherMotherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
