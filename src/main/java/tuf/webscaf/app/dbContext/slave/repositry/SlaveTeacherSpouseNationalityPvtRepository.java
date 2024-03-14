package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherSpouseNationalityPvtEntity, Long> {

    Mono<SlaveTeacherSpouseNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
