package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherHobbyPvtRepository extends ReactiveCrudRepository<SlaveTeacherFatherHobbyPvtEntity, Long> {

    Mono<SlaveTeacherFatherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
