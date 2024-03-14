package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherNationalityPvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentMotherNationalityPvtEntity, Long> {
    Mono<SlaveStudentMotherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
