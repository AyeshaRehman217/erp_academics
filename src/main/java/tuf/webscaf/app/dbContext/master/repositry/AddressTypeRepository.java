package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.AddressTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressTypeRepository extends ReactiveCrudRepository<AddressTypeEntity, Long> {
    Mono<AddressTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<AddressTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<AddressTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<AddressTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<AddressTypeEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}
