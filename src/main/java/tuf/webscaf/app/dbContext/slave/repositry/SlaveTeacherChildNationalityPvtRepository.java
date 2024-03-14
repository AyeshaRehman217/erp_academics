package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherChildNationalityPvtEntity, Long> {

    Mono<SlaveTeacherChildNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
