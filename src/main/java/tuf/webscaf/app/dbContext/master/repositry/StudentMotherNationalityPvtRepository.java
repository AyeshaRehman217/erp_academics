package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentMotherNationalityPvtRepository extends ReactiveCrudRepository<StudentMotherNationalityPvtEntity, Long> {
    Mono<StudentMotherNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<StudentMotherNationalityPvtEntity> findAllByStudentMotherUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID studentMotherUUID, List<UUID> uuids);

    Flux<StudentMotherNationalityPvtEntity> findAllByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);

    Mono<StudentMotherNationalityPvtEntity> findFirstByStudentMotherUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID studentMotherUUID, UUID nationalityUUID);

    Mono<StudentMotherNationalityPvtEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);

}
