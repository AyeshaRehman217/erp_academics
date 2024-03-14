package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherGuardianNationalityPvtEntity, Long> {

    Mono<SlaveTeacherGuardianNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
