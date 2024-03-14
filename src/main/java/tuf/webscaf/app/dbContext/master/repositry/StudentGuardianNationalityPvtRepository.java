package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianNationalityPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGuardianNationalityPvtRepository extends ReactiveCrudRepository<StudentGuardianNationalityPvtEntity, Long> {
    Mono<StudentGuardianNationalityPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianNationalityPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianNationalityPvtEntity> findFirstByNationalityUUIDAndDeletedAtIsNull(UUID nationalityUUID);

    Flux<StudentGuardianNationalityPvtEntity> findAllByStudentGuardianUUIDAndNationalityUUIDInAndDeletedAtIsNull(UUID stdGuardianUUID, List<UUID> nationalityUUID);

    Flux<StudentGuardianNationalityPvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID stdGuardianId);

    Mono<StudentGuardianNationalityPvtEntity> findFirstByStudentGuardianUUIDAndNationalityUUIDAndDeletedAtIsNull(UUID stdGuardianUUID, UUID nationalityUUID);

    Mono<StudentGuardianNationalityPvtEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
