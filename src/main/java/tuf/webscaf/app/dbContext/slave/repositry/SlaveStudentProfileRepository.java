package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentProfileRepository extends ReactiveCrudRepository<SlaveStudentProfileEntity, Long> {
    Flux<SlaveStudentProfileEntity> findAllByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(String firstName, String lastName, String description, String telephoneNo, String nic, Pageable pageable);

    Flux<SlaveStudentProfileEntity> findAllByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String firstName, Boolean status1, String lastName, Boolean status2, String description, Boolean status3, String telephoneNo, Boolean status4, String nic, Boolean status5);

    Mono<Long> countByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String firstName, Boolean status1, String lastName, Boolean status2, String description, Boolean status3, String telephoneNo, Boolean status4, String nic, Boolean status5);

    Mono<Long> countByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(String firstName, String lastName, String description, String telephoneNo, String nic);

    Mono<SlaveStudentProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentProfileEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

}
