package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingAcademicHistoryRepository extends ReactiveCrudRepository<SlaveStudentSiblingAcademicHistoryEntity, Long> {
    Flux<SlaveStudentSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String grade);

    Flux<SlaveStudentSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, Boolean status);

    Flux<SlaveStudentSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentSiblingUUID);

    Flux<SlaveStudentSiblingAcademicHistoryEntity> findAllByGradeContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, String grade, UUID studentSiblingUUID, Boolean status);

    Mono<SlaveStudentSiblingAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<Long> countByGradeContainingIgnoreCaseAndDeletedAtIsNull(String grade);

    Mono<Long> countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String grade, Boolean status);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(String grade, UUID studentSiblingUUID);

    Mono<Long> countByGradeContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(String grade, UUID studentSiblingUUID, Boolean status);

    Mono<SlaveStudentSiblingAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    //Find By Country uuid In Config Module
    Mono<SlaveStudentSiblingAcademicHistoryEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveStudentSiblingAcademicHistoryEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentSiblingAcademicHistoryEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
