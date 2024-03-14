package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSubjectEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSubjectRepository extends ReactiveCrudRepository<SlaveTeacherSubjectEntity, Long> {
    Flux<SlaveTeacherSubjectEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherSubjectEntity> findAllByDeletedAtIsNullAndStatus(Pageable pageable, Boolean status);

    Mono<SlaveTeacherSubjectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByDeletedAtIsNullAndStatus(Boolean status);

    Mono<SlaveTeacherSubjectEntity> findByIdAndDeletedAtIsNull(Long id);

}
