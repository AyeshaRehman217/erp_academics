package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherSpouseAilmentPvtEntity, Long> {

    Mono<SlaveTeacherSpouseAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
