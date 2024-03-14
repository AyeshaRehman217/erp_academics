package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherChildLanguagePvtRepository extends ReactiveCrudRepository<TeacherChildLanguagePvtEntity, Long> {

    Mono<TeacherChildLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherChildLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherChildLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherChildLanguagePvtEntity> findAllByTeacherChildUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherChildUUID, List<UUID> languageUUID);

    Flux<TeacherChildLanguagePvtEntity> findAllByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    Mono<TeacherChildLanguagePvtEntity> findFirstByTeacherChildUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherChildUUID, UUID languageUUID);

    Mono<TeacherChildLanguagePvtEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);
}
