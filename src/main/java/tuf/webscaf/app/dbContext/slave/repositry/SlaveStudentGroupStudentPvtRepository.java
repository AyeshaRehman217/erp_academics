package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGroupStudentPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGroupStudentPvtRepository extends ReactiveCrudRepository<SlaveStudentGroupStudentPvtEntity, Long> {
    Mono<SlaveStudentGroupStudentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
