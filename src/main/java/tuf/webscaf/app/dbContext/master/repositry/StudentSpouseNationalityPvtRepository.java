package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSpouseNationalityPvtRepository extends ReactiveCrudRepository<StudentSpouseNationalityPvtEntity, Long> {
    Mono<StudentSpouseNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<StudentSpouseNationalityPvtEntity> findAllByStudentSpouseUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> uuids);

    Flux<StudentSpouseNationalityPvtEntity> findAllByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<StudentSpouseNationalityPvtEntity> findFirstByStudentSpouseUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID nationalityUUID);

    Mono<StudentSpouseNationalityPvtEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

}
