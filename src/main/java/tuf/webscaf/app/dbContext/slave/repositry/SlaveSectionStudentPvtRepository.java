package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSectionStudentPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSectionStudentPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveSectionStudentPvtRepository extends ReactiveCrudRepository<SlaveSectionStudentPvtEntity,Long>, SlaveCustomSectionStudentPvtRepository {
    Mono<SlaveSectionStudentPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSectionStudentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

}
