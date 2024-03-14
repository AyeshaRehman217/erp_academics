package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildHobbyPvtRepository extends ReactiveCrudRepository<SlaveTeacherChildHobbyPvtEntity, Long> {

    Mono<SlaveTeacherChildHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
