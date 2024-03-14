package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherHobbyPvtEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface StudentMotherHobbyPvtRepository extends ReactiveCrudRepository<StudentMotherHobbyPvtEntity, Long> {
    Mono<StudentMotherHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherHobbyPvtEntity> findFirstByStudentMotherUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID studentMotherUUID, UUID hobbyUUID);

    Flux<StudentMotherHobbyPvtEntity> findAllByStudentMotherUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID studentMotherUUID, List<UUID> ids);

    Flux<StudentMotherHobbyPvtEntity> findAllByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);

    Mono<StudentMotherHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentMotherHobbyPvtEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);
}
