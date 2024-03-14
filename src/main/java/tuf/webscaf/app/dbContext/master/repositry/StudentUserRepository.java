package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentUserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentUserRepository extends ReactiveCrudRepository<StudentUserEntity, Long> {
    Mono<StudentUserEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentUserEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<StudentUserEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);

    Mono<StudentUserEntity> findFirstByUserUUIDAndDeletedAtIsNull(UUID userUUID);

    Mono<StudentUserEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);
}
