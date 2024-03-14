package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentNationalityPvtEntity, Long> {
    Mono<SlaveStudentNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
