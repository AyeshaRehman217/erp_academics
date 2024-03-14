package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.PloPeoPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface PloPeoPvtRepository extends ReactiveCrudRepository<PloPeoPvtEntity, Long> {
    Mono<PloPeoPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<PloPeoPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<PloPeoPvtEntity> findFirstByPeoUUIDAndDeletedAtIsNull(UUID peoUUID);

    Mono<PloPeoPvtEntity> findFirstByPloUUIDAndDeletedAtIsNull(UUID ploUUID);

    Flux<PloPeoPvtEntity> findAllByPloUUIDAndDeletedAtIsNull(UUID ploUUID);

    Mono<PloPeoPvtEntity> findFirstByPloUUIDAndPeoUUIDAndDeletedAtIsNull(UUID ploUUID, UUID peoUUID);

    Flux<PloPeoPvtEntity> findAllByPloUUIDAndPeoUUIDInAndDeletedAtIsNull(UUID ploUUID, List<UUID> ids);
}
