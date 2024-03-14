package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherHobbyPvtRepository extends ReactiveCrudRepository<SlaveTeacherHobbyPvtEntity, Long> {
    Mono<SlaveTeacherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
