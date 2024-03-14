package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.ContactTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactTypeRepository extends ReactiveCrudRepository<ContactTypeEntity, Long> {
    Mono<ContactTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<ContactTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<ContactTypeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuid);

    Mono<ContactTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<ContactTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
