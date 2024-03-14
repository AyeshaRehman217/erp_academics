package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.ContactCategoryEntity;

import java.util.UUID;

@Repository
public interface ContactCategoryRepository extends ReactiveCrudRepository<ContactCategoryEntity, Long> {
    Mono<ContactCategoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<ContactCategoryEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<ContactCategoryEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);

    Mono<ContactCategoryEntity> findFirstBySlugAndDeletedAtIsNull(String slug);

    Mono<ContactCategoryEntity> findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(String slug,UUID uuid);
}
