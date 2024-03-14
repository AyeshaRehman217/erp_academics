package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TimetableChangeReasonEntity;

import java.util.UUID;

@Repository
public interface TimetableChangeReasonRepository extends ReactiveCrudRepository<TimetableChangeReasonEntity, Long> {
    Mono<TimetableChangeReasonEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TimetableChangeReasonEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

//    Mono<TimetableChangeReasonEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);
//
//    Mono<TimetableChangeReasonEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);


}
