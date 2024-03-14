package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.LectureTypeEntity;

import java.util.UUID;

@Repository
public interface LectureTypeRepository extends ReactiveCrudRepository<LectureTypeEntity, Long> {
    Mono<LectureTypeEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<LectureTypeEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<LectureTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<LectureTypeEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
