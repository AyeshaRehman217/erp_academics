package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentSiblingFinancialHistoryEntity, Long> {
    Flux<SlaveStudentSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String description, String assetName);

    Flux<SlaveStudentSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable,String description, Boolean status, String assetName, Boolean status2);

    Flux<SlaveStudentSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(Pageable pageable,String description, UUID studentSiblingUUID, String assetName, UUID studentSiblingUUID2);

    Flux<SlaveStudentSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable,String description, UUID studentSiblingUUID, Boolean status, String assetName, UUID studentSiblingUUID2, Boolean status2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(String description,String assetName);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String description, Boolean status, String assetName, Boolean status2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String description, UUID studentSiblingUUID, String assetName, UUID studentSiblingUUID2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String description, UUID studentSiblingUUID, Boolean status, String assetName, UUID studentSiblingUUID2, Boolean status2);

    Mono<SlaveStudentSiblingFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSiblingFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentSiblingFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
