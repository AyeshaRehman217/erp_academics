package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherGuardianFinancialHistoryEntity, Long> {

    Flux<SlaveTeacherGuardianFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherGuardianFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveTeacherGuardianFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherGuardianUUID, String description,UUID teacherGuardianUUID2);

    Flux<SlaveTeacherGuardianFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherGuardianUUID, Boolean status, String description,UUID teacherGuardianUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String assetName,UUID teacherGuardianUUID, String description,UUID teacherGuardianUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherGuardianUUID, Boolean status, String description,UUID teacherGuardianUUID2, Boolean status2);

    Mono<SlaveTeacherGuardianFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherGuardianFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
