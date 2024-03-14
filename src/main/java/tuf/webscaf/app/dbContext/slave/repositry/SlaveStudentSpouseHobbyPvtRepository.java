package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseHobbyPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentSpouseHobbyPvtEntity, Long> {

    Mono<SlaveStudentSpouseHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
