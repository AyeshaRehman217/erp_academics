package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOfferedEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomCourseOfferedSubjectRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectOfferedRepository;

import java.util.UUID;

@Repository
public interface SlaveCourseOfferedSubjectRepository extends ReactiveCrudRepository<SlaveSubjectOfferedEntity, Long>, SlaveCustomCourseOfferedSubjectRepository, SlaveCustomSubjectOfferedRepository {
    Mono<SlaveSubjectOfferedEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectOfferedEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
