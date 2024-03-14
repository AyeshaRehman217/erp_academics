package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentSpouseAcademicHistoryEntity, Long> {
    Flux<SlaveStudentSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveStudentSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveStudentSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentSpouseUUID);

    Flux<SlaveStudentSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentSpouseUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(String grade, UUID studentSpouseUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentSpouseUUID, Boolean status);

    Mono<SlaveStudentSpouseAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSpouseAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSpouseAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentSpouseAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentSpouseAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
