package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherNationalityPvtEntity, Long> {
    Mono<SlaveTeacherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
