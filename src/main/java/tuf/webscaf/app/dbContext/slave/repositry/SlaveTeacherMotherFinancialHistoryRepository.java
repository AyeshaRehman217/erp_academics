package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherFinancialHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherFinancialHistoryRepository extends ReactiveCrudRepository<SlaveTeacherMotherFinancialHistoryEntity, Long> {

    Flux<SlaveTeacherMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String assetName, String description);

    Flux<SlaveTeacherMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,Boolean status1, String description,Boolean status2);

    Flux<SlaveTeacherMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherMotherUUID, String description,UUID teacherMotherUUID2);

    Flux<SlaveTeacherMotherFinancialHistoryEntity> findAllByAssetNameContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String assetName,UUID teacherMotherUUID, Boolean status, String description,UUID teacherMotherUUID2, Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String assetName,String description);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String assetName,Boolean status1,String description,Boolean status2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(String assetName,UUID teacherMotherUUID, String description,UUID teacherMotherUUID2);

    Mono<Long> countByAssetNameContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(String assetName,UUID teacherMotherUUID, Boolean status, String description,UUID teacherMotherUUID2, Boolean status2);

    Mono<SlaveTeacherMotherFinancialHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherFinancialHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Currency uuid In Config Module
    Mono<SlaveTeacherMotherFinancialHistoryEntity> findFirstByCurrencyUUIDAndDeletedAtIsNull(UUID currencyUUID);
}
