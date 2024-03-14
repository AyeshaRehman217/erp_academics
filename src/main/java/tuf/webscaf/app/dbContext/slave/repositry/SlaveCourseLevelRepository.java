package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseLevelEntity;

import java.util.UUID;

@Repository
public interface SlaveCourseLevelRepository extends ReactiveCrudRepository<SlaveCourseLevelEntity, Long> {
    Mono<SlaveCourseLevelEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveCourseLevelEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveCourseLevelEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Flux<SlaveCourseLevelEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

//  used in seeder
    Mono<SlaveCourseLevelEntity> findByNameAndDeletedAtIsNull(String name);

}
