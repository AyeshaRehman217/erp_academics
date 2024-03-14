package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSiblingLanguagePvtRepository extends ReactiveCrudRepository<StudentSiblingLanguagePvtEntity, Long> {

    Mono<StudentSiblingLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSiblingLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSiblingLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentSiblingLanguagePvtEntity> findAllByStudentSiblingUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentSiblingUUID, List<UUID> languageUUID);

    Flux<StudentSiblingLanguagePvtEntity> findAllByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

    Mono<StudentSiblingLanguagePvtEntity> findFirstByStudentSiblingUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentSiblingUUID, UUID languageUUID);

    Mono<StudentSiblingLanguagePvtEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);
}
