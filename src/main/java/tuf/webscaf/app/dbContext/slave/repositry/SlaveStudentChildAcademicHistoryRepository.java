package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentChildAcademicHistoryEntity, Long> {

    Mono<SlaveStudentChildAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveStudentChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveStudentChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentChildUUID);

    Flux<SlaveStudentChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentChildUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(String grade, UUID studentChildUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentChildUUID, Boolean status);

    Mono<SlaveStudentChildAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<SlaveStudentChildAcademicHistoryEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentChildAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveStudentChildAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentChildAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

}
