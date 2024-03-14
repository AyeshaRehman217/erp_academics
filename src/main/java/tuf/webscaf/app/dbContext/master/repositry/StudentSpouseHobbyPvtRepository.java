package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseHobbyPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface StudentSpouseHobbyPvtRepository extends ReactiveCrudRepository<StudentSpouseHobbyPvtEntity, Long> {
    Mono<StudentSpouseHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseHobbyPvtEntity> findFirstByStudentSpouseUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID hobbyUUID);

    Flux<StudentSpouseHobbyPvtEntity> findAllByStudentSpouseUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> ids);

    Flux<StudentSpouseHobbyPvtEntity> findAllByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentSpouseHobbyPvtEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
