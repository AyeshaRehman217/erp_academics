package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentFatherFinancialHistoryEntity, Long> {
    Mono<SlaveStudentFatherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveStudentFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName, Boolean status1, String description, Boolean status2);

    Flux<SlaveStudentFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String assetName, UUID studentFatherUUID, String description, UUID studentFatherUUID2);

    Flux<SlaveStudentFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName, UUID studentFatherUUID, Boolean status, String description, UUID studentFatherUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName, String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName, Boolean status1, String description, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String assetName, UUID studentFatherUUID, String description, UUID studentFatherUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(String assetName, UUID studentFatherUUID, Boolean status, String description, UUID studentFatherUUID2, Boolean status2);

    Mono<SlaveStudentFatherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentFatherFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
