package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentUserEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.*;

import java.util.UUID;

@Repository
public interface SlaveStudentUserRepository extends ReactiveCrudRepository<SlaveStudentUserEntity, Long>{

    Flux<SlaveStudentUserEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveStudentUserEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveStudentUserEntity> findByIdAndDeletedAtIsNull(Long id);

}
