package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherFatherAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    //Searching Data based on Status Filter
    Flux<SlaveTeacherFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,Boolean status);

    Flux<SlaveTeacherFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(Pageable pageable, String grade,UUID teacherFatherUUID);

    Flux<SlaveTeacherFatherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,UUID teacherFatherUUID,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    //Count Data based on Status Filter
    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(String grade,UUID teacherFatherUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(String grade,UUID teacherFatherUUID,Boolean status);

    Mono<SlaveTeacherFatherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherFatherAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherFatherAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherFatherAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
