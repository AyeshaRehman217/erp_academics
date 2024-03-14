package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentLanguagePvtRepository extends ReactiveCrudRepository<StudentLanguagePvtEntity, Long> {

    Mono<StudentLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentLanguagePvtEntity> findAllByStudentUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentUUID, List<UUID> languageUUID);

    Flux<StudentLanguagePvtEntity> findAllByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentLanguagePvtEntity> findFirstByStudentUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentUUID, UUID languageUUID);

    Mono<StudentLanguagePvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);
}
