package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherAilmentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherAilmentPvtRepository extends ReactiveCrudRepository<SlaveTeacherFatherAilmentPvtEntity, Long> {
    Mono<SlaveTeacherFatherAilmentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherAilmentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
