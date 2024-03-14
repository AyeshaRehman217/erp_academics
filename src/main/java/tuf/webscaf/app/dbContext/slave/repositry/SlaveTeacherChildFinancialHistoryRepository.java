package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherChildFinancialHistoryEntity, Long> {
    Flux<SlaveTeacherChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveTeacherChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherChildUUID, String description,UUID teacherChildUUID2);

    Flux<SlaveTeacherChildFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherChildUUID, Boolean status, String description,UUID teacherChildUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(String assetName,UUID teacherChildUUID, String description,UUID teacherChildUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherChildUUID, Boolean status, String description,UUID teacherChildUUID2, Boolean status2);

    Mono<SlaveTeacherChildFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherChildFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherChildFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
