package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGuardianHobbyPvtRepository extends ReactiveCrudRepository<StudentGuardianHobbyPvtEntity, Long> {
    Mono<StudentGuardianHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<StudentGuardianHobbyPvtEntity> findAllByStudentGuardianUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID stdGuardianUUID, List<UUID> hobbyUUID);

    Flux<StudentGuardianHobbyPvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianId);

    Mono<StudentGuardianHobbyPvtEntity> findFirstByStudentGuardianUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID stdGuardianUUID, UUID hobbyUUID);

    Mono<StudentGuardianHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentGuardianHobbyPvtEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
