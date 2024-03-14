package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherNationalityPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentFatherNationalityPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherNationalityPvtRepository extends ReactiveCrudRepository<SlaveStudentFatherNationalityPvtEntity, Long>,
        SlaveCustomStudentFatherNationalityPvtRepository {
    Mono<SlaveStudentFatherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
