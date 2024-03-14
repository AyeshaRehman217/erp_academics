package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentChildFinancialHistoryEntity, Long> {
    
    Mono<SlaveStudentChildFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentChildFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);

    Mono<SlaveStudentChildFinancialHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Flux<SlaveStudentChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveStudentChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveStudentChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentChildUUID, String description,UUID studentChildUUID2);

    Flux<SlaveStudentChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentChildUUID, Boolean status, String description,UUID studentChildUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(String assetName,UUID studentChildUUID, String description,UUID studentChildUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID studentChildUUID, Boolean status, String description,UUID studentChildUUID2, Boolean status2);

}
