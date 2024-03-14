package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFacultyEntity;

import java.util.UUID;

@Repository
public interface SlaveFacultyRepository extends ReactiveCrudRepository<SlaveFacultyEntity, Long> {
    Mono<SlaveFacultyEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveFacultyEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String description);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String name, String description);

    Flux<SlaveFacultyEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String description, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String description, Boolean status2);

    Flux<SlaveFacultyEntity> findAllByNameContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, UUID campusUUID1, String description, Boolean status2, UUID campusUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNull(String name, Boolean status, UUID campusUUID1, String description, Boolean status2, UUID campusUUID2);

    Flux<SlaveFacultyEntity> findAllByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(Pageable pageable, String name, UUID campusUUID1, String description, UUID campusUUID2);

    Mono<Long> countByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(String name, UUID campusUUID1, String description, UUID campusUUID2);

    //    used in seeder
    Mono<SlaveFacultyEntity> findByNameAndDeletedAtIsNull(String name);
}
