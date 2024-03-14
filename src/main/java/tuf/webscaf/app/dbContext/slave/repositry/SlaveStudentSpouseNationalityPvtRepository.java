package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentSpouseNationalityPvtEntity, Long> {

    Mono<SlaveStudentSpouseNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
