package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentNationalityPvtRepository extends ReactiveCrudRepository<StudentNationalityPvtEntity, Long> {
    Mono<StudentNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Mono<StudentNationalityPvtEntity> findFirstByStudentUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherFatherUUID, UUID nationalityUUID);

    Flux<StudentNationalityPvtEntity> findAllByStudentUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherFatherUUID, List<UUID> uuids);

    Flux<StudentNationalityPvtEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    Mono<StudentNationalityPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID stdUUID);

}
