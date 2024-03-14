package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherGuardianAilmentPvtEntity, Long> {

    Mono<SlaveTeacherGuardianAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
