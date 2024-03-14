package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherAcademicHistoryRepository extends ReactiveCrudRepository<SlaveTeacherMotherAcademicHistoryEntity, Long> {
    Flux<SlaveTeacherMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveTeacherMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveTeacherMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherMotherUUID);

    Flux<SlaveTeacherMotherAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID teacherMotherUUID, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(String grade, UUID teacherMotherUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(String grade, UUID teacherMotherUUID, Boolean status);

    Mono<SlaveTeacherMotherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherMotherAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherMotherAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherMotherAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
