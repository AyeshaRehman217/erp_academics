package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSiblingFinancialHistoryEntity, Long> {
    Flux<SlaveTeacherSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable,String description,String assetName);

    Flux<SlaveTeacherSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable,String description, Boolean status, String assetName, Boolean status2);

    Flux<SlaveTeacherSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(Pageable pageable,String description, UUID teacherSiblingUUID, String assetName, UUID teacherSiblingUUID2);

    Flux<SlaveTeacherSiblingFinancialHistoryEntity> findAllByDescriptionContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable,String description, UUID teacherSiblingUUID, Boolean status, String assetName, UUID teacherSiblingUUID2, Boolean status2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndDeletedAtIsNull(String description,String assetName);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String description, Boolean status, String assetName, Boolean status2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(String description, UUID teacherSiblingUUID, String assetName, UUID teacherSiblingUUID2);

    Mono<Long> countByDescriptionContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNullOrAssetNameContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(String description, UUID teacherSiblingUUID, Boolean status, String assetName, UUID teacherSiblingUUID2, Boolean status2);

    Mono<SlaveTeacherSiblingFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSiblingFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherSiblingFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
