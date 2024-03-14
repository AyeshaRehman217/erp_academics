package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.HolidayTypeEntity;

import java.util.UUID;

@Repository
public interface HolidayTypeRepository extends ReactiveCrudRepository <HolidayTypeEntity, Long>{

    Mono<HolidayTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<HolidayTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<HolidayTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<HolidayTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
