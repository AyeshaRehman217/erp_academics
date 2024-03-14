package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherChildAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveTeacherChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherChildUUID);

    Flux<SlaveTeacherChildAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherChildUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherChildUUIDAndDeletedAtIsNull(String grade, UUID teacherChildUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherChildUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherChildUUID, Boolean status);

    Mono<SlaveTeacherChildAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherChildAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherChildAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherChildAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherChildAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
