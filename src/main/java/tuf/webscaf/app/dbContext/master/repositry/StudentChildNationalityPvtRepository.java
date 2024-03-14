package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentChildNationalityPvtRepository extends ReactiveCrudRepository<StudentChildNationalityPvtEntity, Long> {
    
    Mono<StudentChildNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<StudentChildNationalityPvtEntity> findAllByStudentChildUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> uuids);

    Flux<StudentChildNationalityPvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<StudentChildNationalityPvtEntity> findFirstByStudentChildUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID nationalityUUID);

    Mono<StudentChildNationalityPvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

}
