package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentFinancialHistoryEntity, Long> {
    Flux<SlaveStudentFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveStudentFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveStudentFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentUUID, String description,UUID studentUUID2);

    Flux<SlaveStudentFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentUUID, Boolean status, String description,UUID studentUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(String assetName,UUID studentUUID, String description,UUID studentUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID studentUUID, Boolean status, String description,UUID studentUUID2, Boolean status2);

    Mono<SlaveStudentFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
