package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCampusCourseOfferedPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveCampusCourseOfferedPvtRepository extends ReactiveCrudRepository<SlaveCampusCourseOfferedPvtEntity, Long> {

    Mono<SlaveCampusCourseOfferedPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCampusCourseOfferedPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
