package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherAcademicRecordEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherAcademicRecordRepository extends ReactiveCrudRepository<SlaveTeacherAcademicRecordEntity, Long> {
    Flux<SlaveTeacherAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    //Searching Data based on Status Filter
    Flux<SlaveTeacherAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(Pageable pageable, String grade,UUID teacherUUID);

    Flux<SlaveTeacherAcademicRecordEntity> findAllByGradeContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade,UUID teacherUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    //Count Data based on Status Filter
    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherUUIDAndDeletedAtIsNull(String grade, UUID teacherUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherUUID, Boolean status);

    Mono<SlaveTeacherAcademicRecordEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherAcademicRecordEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherAcademicRecordEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherAcademicRecordEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherAcademicRecordEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
