package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentMotherAcademicHistoryEntity, Long> {
    Flux<SlaveStudentMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveStudentMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveStudentMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentMotherUUID);

    Flux<SlaveStudentMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentMotherUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(String grade, UUID studentMotherUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentMotherUUID, Boolean status);

    Mono<SlaveStudentMotherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentMotherAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentMotherAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentMotherAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
