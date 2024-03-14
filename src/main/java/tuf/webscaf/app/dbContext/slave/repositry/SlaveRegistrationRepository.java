package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveRegistrationEntity;

import java.util.UUID;

@Repository
public interface SlaveRegistrationRepository extends ReactiveCrudRepository<SlaveRegistrationEntity, Long> {
    Mono<SlaveRegistrationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveRegistrationEntity> findAllByRegistrationNoContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String registrationNo);

    Mono<Long> countByRegistrationNoContainingIgnoreCaseAndDeletedAtIsNull(String registrationNo);


    Flux<SlaveRegistrationEntity> findAllByRegistrationNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String registrationNo, Boolean status);

    Mono<Long> countByRegistrationNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String registrationNo, Boolean status);

    Mono<SlaveRegistrationEntity> findByIdAndDeletedAtIsNull(Long id);
}
