package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentAcademicRecordEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentAcademicRecordRepository extends ReactiveCrudRepository<SlaveStudentAcademicRecordEntity, Long> {
    Mono<SlaveStudentAcademicRecordEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    //Searching Data based on Status Filter
    Flux<SlaveStudentAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Flux<SlaveStudentAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentUUID, Boolean status);

    Flux<SlaveStudentAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(String grade, UUID studentUUID);

    //Count Data based on Status Filter
    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade,Boolean status);

    Mono<SlaveStudentAcademicRecordEntity> findByIdAndDeletedAtIsNull(Long id);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentAcademicRecordEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentAcademicRecordEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentAcademicRecordEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
