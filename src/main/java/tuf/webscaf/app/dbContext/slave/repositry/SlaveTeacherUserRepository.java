package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherUserEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherUserRepository extends ReactiveCrudRepository<SlaveTeacherUserEntity, Long>{

    Flux<SlaveTeacherUserEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<SlaveTeacherUserEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<SlaveTeacherUserEntity> findByIdAndDeletedAtIsNull(Long id);

}
