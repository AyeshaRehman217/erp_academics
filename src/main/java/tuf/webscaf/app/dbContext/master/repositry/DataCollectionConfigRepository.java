package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.DataCollectionConfigEntity;

import java.util.UUID;

@Repository
public interface DataCollectionConfigRepository extends ReactiveCrudRepository<DataCollectionConfigEntity, Long> {
    Mono<DataCollectionConfigEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<DataCollectionConfigEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<DataCollectionConfigEntity> findFirstByTableNameAndDeletedAtIsNull(String tableName);

    Mono<DataCollectionConfigEntity> findFirstByTableNameAndDeletedAtIsNullAndIdIsNot(String key, Long id);

    Mono<DataCollectionConfigEntity> findFirstByTableNameAndDeletedAtIsNullAndUuidIsNot(String key, UUID uuid);
}
