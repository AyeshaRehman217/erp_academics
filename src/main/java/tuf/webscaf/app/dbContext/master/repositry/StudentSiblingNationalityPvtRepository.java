package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSiblingNationalityPvtRepository extends ReactiveCrudRepository<StudentSiblingNationalityPvtEntity, Long> {
    Mono<StudentSiblingNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<StudentSiblingNationalityPvtEntity> findAllByStudentSiblingUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID stdSiblingUUID, List<UUID> nationalityUUID);

    Flux<StudentSiblingNationalityPvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID stdSiblingId);

    Mono<StudentSiblingNationalityPvtEntity> findFirstByStudentSiblingUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID stdSiblingUUID, UUID nationalityUUID);

    Mono<StudentSiblingNationalityPvtEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUuid);

}
