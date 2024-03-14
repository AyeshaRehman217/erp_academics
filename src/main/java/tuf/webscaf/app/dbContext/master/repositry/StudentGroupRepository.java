package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGroupEntity;

import java.util.UUID;

@Repository
public interface StudentGroupRepository extends ReactiveCrudRepository<StudentGroupEntity, Long> {

    Mono<StudentGroupEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGroupEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGroupEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Mono<StudentGroupEntity> findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(String name, UUID uuid);
}
