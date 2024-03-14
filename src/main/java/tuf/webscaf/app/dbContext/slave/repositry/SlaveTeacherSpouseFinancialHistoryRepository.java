package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSpouseFinancialHistoryEntity, Long> {

    Flux<SlaveTeacherSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveTeacherSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherSpouseUUID, String description,UUID teacherSpouseUUID2);

    Flux<SlaveTeacherSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherSpouseUUID, Boolean status, String description,UUID teacherSpouseUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(String assetName,UUID teacherSpouseUUID, String description,UUID teacherSpouseUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherSpouseUUID, Boolean status, String description,UUID teacherSpouseUUID2, Boolean status2);

    Mono<SlaveTeacherSpouseFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherSpouseFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
