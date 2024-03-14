package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherFinancialHistoryEntity, Long> {
    Flux<SlaveTeacherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveTeacherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherUUID, String description,UUID teacherUUID2);

    Flux<SlaveTeacherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherUUID, Boolean status, String description,UUID teacherUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(String assetName,UUID teacherUUID, String description,UUID teacherUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherUUID, Boolean status, String description,UUID teacherUUID2, Boolean status2);

    Mono<SlaveTeacherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
