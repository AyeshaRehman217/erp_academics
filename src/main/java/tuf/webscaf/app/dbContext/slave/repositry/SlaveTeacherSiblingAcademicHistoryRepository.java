package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherSiblingAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveTeacherSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherSiblingUUID);

    Flux<SlaveTeacherSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherSiblingUUID, Boolean status);

    Mono<SlaveTeacherSiblingAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherSiblingUUIDAndDeletedAtIsNull(String grade, UUID teacherSiblingUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherSiblingUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherSiblingUUID, Boolean status);

    Mono<SlaveTeacherSiblingAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherSiblingAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherSiblingAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherSiblingAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
