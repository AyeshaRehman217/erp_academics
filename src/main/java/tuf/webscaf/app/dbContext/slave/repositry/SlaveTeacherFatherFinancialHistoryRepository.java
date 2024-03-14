package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherFatherFinancialHistoryEntity, Long> {
    Flux<SlaveTeacherFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName, Boolean status1, String description, Boolean status2);

    Flux<SlaveTeacherFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String assetName, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2);

    Flux<SlaveTeacherFatherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName, UUID teacherFatherUUID, Boolean status, String description, UUID teacherFatherUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName, String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName, Boolean status1, String description, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String assetName, UUID teacherFatherUUID, String description, UUID teacherFatherUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(String assetName, UUID teacherFatherUUID, Boolean status, String description, UUID teacherFatherUUID2, Boolean status2);

    Mono<SlaveTeacherFatherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherFatherFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
