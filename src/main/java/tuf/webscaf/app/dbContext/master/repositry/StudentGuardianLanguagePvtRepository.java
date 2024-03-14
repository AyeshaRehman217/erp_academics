package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGuardianLanguagePvtRepository extends ReactiveCrudRepository<StudentGuardianLanguagePvtEntity, Long> {

    Mono<StudentGuardianLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentGuardianLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGuardianLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentGuardianLanguagePvtEntity> findAllByStudentGuardianUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentGuardianUUID, List<UUID> languageUUID);

    Flux<StudentGuardianLanguagePvtEntity> findAllByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);

    Mono<StudentGuardianLanguagePvtEntity> findFirstByStudentGuardianUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentGuardianUUID, UUID languageUUID);

    Mono<StudentGuardianLanguagePvtEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);
}
