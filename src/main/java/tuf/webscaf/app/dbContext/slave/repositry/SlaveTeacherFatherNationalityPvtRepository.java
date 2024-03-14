package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherNationalityPvtRepository extends ReactiveCrudRepository<SlaveTeacherFatherNationalityPvtEntity, Long> {
    Mono<SlaveTeacherFatherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
