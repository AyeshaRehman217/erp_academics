package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseFinancialHistoryRepository extends ReactiveCrudRepository<SlaveStudentSpouseFinancialHistoryEntity, Long> {

    Flux<SlaveStudentSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveStudentSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveStudentSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherSpouseUUID, String description,UUID teacherSpouseUUID2);

    Flux<SlaveStudentSpouseFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherSpouseUUID, Boolean status, String description,UUID teacherSpouseUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(String assetName,UUID teacherSpouseUUID, String description,UUID teacherSpouseUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherSpouseUUID, Boolean status, String description,UUID teacherSpouseUUID2, Boolean status2);

    Mono<SlaveStudentSpouseFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSpouseFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveStudentSpouseFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
