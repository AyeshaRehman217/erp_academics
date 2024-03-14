package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherMotherNationalityPvtEntity, Long> {

    Mono<SlaveTeacherMotherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
