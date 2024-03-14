package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.HolidayEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayRepository extends ReactiveCrudRepository <HolidayEntity, Long>{

    Mono<HolidayEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<HolidayEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<HolidayEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<HolidayEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Flux<HolidayEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);
}