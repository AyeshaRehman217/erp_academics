package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSiblingLanguagePvtRepository extends ReactiveCrudRepository<TeacherSiblingLanguagePvtEntity, Long> {

    Mono<TeacherSiblingLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSiblingLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSiblingLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherSiblingLanguagePvtEntity> findAllByTeacherSiblingUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherSiblingUUID, List<UUID> languageUUID);

    Flux<TeacherSiblingLanguagePvtEntity> findAllByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    Mono<TeacherSiblingLanguagePvtEntity> findFirstByTeacherSiblingUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID, UUID languageUUID);

    Mono<TeacherSiblingLanguagePvtEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);
}
