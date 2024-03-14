package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.FacultyEntity;

import java.util.UUID;

@Repository
public interface FacultyRepository extends ReactiveCrudRepository<FacultyEntity, Long> {
    Mono<FacultyEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<FacultyEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<FacultyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<FacultyEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

//    Mono<FacultyEntity> findFirstBySlugAndDeletedAtIsNull(String slug);
//
//    Mono<FacultyEntity> findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(String slug, UUID uuid);
}
