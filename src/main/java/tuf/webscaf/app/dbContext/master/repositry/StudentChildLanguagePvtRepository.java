package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentChildLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentChildLanguagePvtRepository extends ReactiveCrudRepository<StudentChildLanguagePvtEntity, Long> {

    Mono<StudentChildLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentChildLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentChildLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentChildLanguagePvtEntity> findAllByStudentChildUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentChildUUID, List<UUID> languageUUID);

    Flux<StudentChildLanguagePvtEntity> findAllByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    Mono<StudentChildLanguagePvtEntity> findFirstByStudentChildUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentChildUUID, UUID languageUUID);

    Mono<StudentChildLanguagePvtEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);
}
