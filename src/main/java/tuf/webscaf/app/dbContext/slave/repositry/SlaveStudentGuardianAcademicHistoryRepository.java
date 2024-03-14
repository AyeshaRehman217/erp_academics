package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentGuardianAcademicHistoryEntity, Long> {
    Flux<SlaveStudentGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveStudentGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveStudentGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentGuardianUUID);

    Flux<SlaveStudentGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentGuardianUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(String grade, UUID studentGuardianUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentGuardianUUID, Boolean status);
    
    Mono<SlaveStudentGuardianAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentGuardianAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentGuardianAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentGuardianAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentGuardianAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
