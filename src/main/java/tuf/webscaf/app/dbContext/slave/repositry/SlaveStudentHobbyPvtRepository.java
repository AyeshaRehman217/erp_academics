package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentHobbyPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomHobbyStudentPvtRepository;

import java.util.UUID;


@Repository
public interface SlaveStudentHobbyPvtRepository extends ReactiveCrudRepository<SlaveStudentHobbyPvtEntity, Long>, SlaveCustomHobbyStudentPvtRepository {

    Mono<SlaveStudentHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
