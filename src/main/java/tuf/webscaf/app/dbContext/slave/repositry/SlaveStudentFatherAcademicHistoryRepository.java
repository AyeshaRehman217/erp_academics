package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentFatherAcademicHistoryEntity, Long> {
    Flux<SlaveStudentFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    //Searching Data based on Status Filter
    Flux<SlaveStudentFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,Boolean status);

    Flux<SlaveStudentFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(Pageable pageable, String grade,UUID studentFatherUUID);

    Flux<SlaveStudentFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,UUID studentFatherUUID,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    //Count Data based on Status Filter
    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(String grade,UUID studentFatherUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(String grade,UUID studentFatherUUID,Boolean status);

    Mono<SlaveStudentFatherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<Long> countByDeletedAtIsNull();

    //Find By Country uuid In Config Module
    Mono<SlaveStudentFatherAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveStudentFatherAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentFatherAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
