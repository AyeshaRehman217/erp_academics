package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildHobbyPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentChildHobbyPvtRepository extends ReactiveCrudRepository<StudentChildHobbyPvtEntity, Long> {
    
    Mono<StudentChildHobbyPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildHobbyPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<StudentChildHobbyPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Flux<StudentChildHobbyPvtEntity> findAllByStudentChildUUIDAndHobbyUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> ids);

    Mono<StudentChildHobbyPvtEntity> findFirstByStudentChildUUIDAndHobbyUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID hobbyUUID);

    Mono<StudentChildHobbyPvtEntity> findFirstByHobbyUUIDAndDeletedAtIsNull(UUID hobbyUUID);

    Mono<StudentChildHobbyPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
