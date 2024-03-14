package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSiblingHobbyPvtRepository extends ReactiveCrudRepository<StudentSiblingHobbyPvtEntity, Long> {
    Mono<StudentSiblingHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<StudentSiblingHobbyPvtEntity> findAllByStudentSiblingUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID stdSiblingUUID, List<UUID> hobbyUUID);

    Flux<StudentSiblingHobbyPvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID stdSiblingId);

    Mono<StudentSiblingHobbyPvtEntity> findFirstByStudentSiblingUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID stdSiblingUUID, UUID hobbyUUID);

    Mono<StudentSiblingHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentSiblingHobbyPvtEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);

}
