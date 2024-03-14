package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherGuardianAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveTeacherGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherGuardianUUID);

    Flux<SlaveTeacherGuardianAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherGuardianUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(String grade, UUID teacherGuardianUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherGuardianUUID, Boolean status);

    Mono<SlaveTeacherGuardianAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherGuardianAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherGuardianAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherGuardianAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
