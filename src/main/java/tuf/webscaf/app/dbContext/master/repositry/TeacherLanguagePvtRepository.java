package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherLanguagePvtRepository extends ReactiveCrudRepository<TeacherLanguagePvtEntity, Long> {

    Mono<TeacherLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherLanguagePvtEntity> findAllByTeacherUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherUUID, List<UUID> languageUUID);

    Flux<TeacherLanguagePvtEntity> findAllByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherLanguagePvtEntity> findFirstByTeacherUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherUUID, UUID languageUUID);

    Mono<TeacherLanguagePvtEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);
}
