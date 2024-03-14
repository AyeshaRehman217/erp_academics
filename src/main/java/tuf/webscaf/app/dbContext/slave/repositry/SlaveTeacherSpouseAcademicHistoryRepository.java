package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSpouseAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable,String grade);

    Flux<SlaveTeacherSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherSpouseUUID);

    Flux<SlaveTeacherSpouseAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherSpouseUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade,Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherSpouseUUIDAndDeletedAtIsNull(String grade, UUID teacherSpouseUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherSpouseUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherSpouseUUID, Boolean status);

    Mono<SlaveTeacherSpouseAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherSpouseAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherSpouseAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherSpouseAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
