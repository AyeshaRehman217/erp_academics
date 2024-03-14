package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherGuardianLanguagePvtRepository extends ReactiveCrudRepository<TeacherGuardianLanguagePvtEntity, Long> {

    Mono<TeacherGuardianLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherGuardianLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherGuardianLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherGuardianLanguagePvtEntity> findAllByTeacherGuardianUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherGuardianUUID, List<UUID> languageUUID);

    Flux<TeacherGuardianLanguagePvtEntity> findAllByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<TeacherGuardianLanguagePvtEntity> findFirstByTeacherGuardianUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID, UUID languageUUID);

    Mono<TeacherGuardianLanguagePvtEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);
}
