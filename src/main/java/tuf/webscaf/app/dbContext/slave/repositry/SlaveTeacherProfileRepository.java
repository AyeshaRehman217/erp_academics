package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherProfileRepository extends ReactiveCrudRepository<SlaveTeacherProfileEntity, Long> {
    Flux<SlaveTeacherProfileEntity> findAllByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String firstName, String lastName, String nic, String telephoneNo);

    Flux<SlaveTeacherProfileEntity> findAllByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String firstName, Boolean status1, String lastName, Boolean status2, String nic, Boolean status3, String telephoneNo, Boolean status4);

    Mono<Long> countByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNull(String firstName, String lastName, String nic, String telephoneNo);

    Mono<Long> countByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String firstName, Boolean status1, String lastName, Boolean status2, String nic, Boolean status3, String telephoneNo, Boolean status4);

    Mono<SlaveTeacherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherProfileEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<SlaveTeacherProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);
}
