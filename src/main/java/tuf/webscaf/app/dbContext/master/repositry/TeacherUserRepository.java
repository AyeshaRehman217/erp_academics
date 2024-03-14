package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherUserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherUserRepository extends ReactiveCrudRepository<TeacherUserEntity, Long> {
    Mono<TeacherUserEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherUserEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherUserEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> uuids);

    Mono<TeacherUserEntity> findFirstByUserUUIDAndDeletedAtIsNull(UUID userUUID);

    Mono<TeacherUserEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
