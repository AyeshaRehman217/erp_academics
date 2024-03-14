package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentFatherHobbyPvtRepository extends ReactiveCrudRepository<StudentFatherHobbyPvtEntity, Long> {
    Mono<StudentFatherHobbyPvtEntity> findFirstByStudentFatherUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID studentFatherUUID, UUID hobbyUUID);

    Flux<StudentFatherHobbyPvtEntity> findAllByStudentFatherUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID studentFatherUUID, List<UUID> ids);

    Flux<StudentFatherHobbyPvtEntity> findAllByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);

    Mono<StudentFatherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentFatherHobbyPvtEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);
}
