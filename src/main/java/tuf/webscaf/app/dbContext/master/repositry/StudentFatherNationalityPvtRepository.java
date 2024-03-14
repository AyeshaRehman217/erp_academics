package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentFatherNationalityPvtRepository extends ReactiveCrudRepository<StudentFatherNationalityPvtEntity, Long> {
    Mono<StudentFatherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Mono<StudentFatherNationalityPvtEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUuid);

    Mono<StudentFatherNationalityPvtEntity> findFirstByStudentFatherUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID studentFatherUUID, UUID nationalityUUID);

    Flux<StudentFatherNationalityPvtEntity> findAllByStudentFatherUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID studentFatherUUID, List<UUID> uuids);

    Flux<StudentFatherNationalityPvtEntity> findAllByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);
}
