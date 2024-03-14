package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentMotherFinancialHistoryEntity, Long> {
    Flux<SlaveStudentMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveStudentMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveStudentMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentMotherUUID, String description,UUID studentMotherUUID2);

    Flux<SlaveStudentMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID studentMotherUUID, Boolean status, String description,UUID studentMotherUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(String assetName,UUID studentMotherUUID, String description,UUID studentMotherUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID studentMotherUUID, Boolean status, String description,UUID studentMotherUUID2, Boolean status2);

    Mono<SlaveStudentMotherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentMotherFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
