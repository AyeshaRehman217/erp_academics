package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianHobbyPvtRepository extends ReactiveCrudRepository<SlaveTeacherGuardianHobbyPvtEntity, Long> {

    Mono<SlaveTeacherGuardianHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
