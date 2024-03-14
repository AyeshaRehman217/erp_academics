package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveEmphasisLevelEntity;

import java.util.UUID;

@Repository
public interface SlaveEmphasisLevelRepository extends ReactiveCrudRepository<SlaveEmphasisLevelEntity, Long> {
    Mono<SlaveEmphasisLevelEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveEmphasisLevelEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    /**
     * Fetch All Records With name and Status Filter
     **/
    Flux<SlaveEmphasisLevelEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status);


    Flux<SlaveEmphasisLevelEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);

}
