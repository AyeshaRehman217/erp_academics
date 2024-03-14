package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherAilmentPvtEntity, Long> {
    Mono<SlaveTeacherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
