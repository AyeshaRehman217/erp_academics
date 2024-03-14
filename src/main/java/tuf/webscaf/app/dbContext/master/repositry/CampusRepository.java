package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CampusEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampusRepository extends ReactiveCrudRepository<CampusEntity, Long> {

    Mono<CampusEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CampusEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<CampusEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> campusList);

    //Check If Name is Unique while storing Data
    Mono<CampusEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    //Check If Name is Unique while Updating Data
    Mono<CampusEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    //Check If Code is Unique while storing Data
    Mono<CampusEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    //Check If Code is Unique while Updating Data
    Mono<CampusEntity> findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String code, UUID uuid);
}
