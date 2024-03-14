package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherMotherLanguagePvtRepository extends ReactiveCrudRepository<TeacherMotherLanguagePvtEntity, Long> {

    Mono<TeacherMotherLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherMotherLanguagePvtEntity> findAllByTeacherMotherUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherMotherUUID, List<UUID> languageUUID);

    Flux<TeacherMotherLanguagePvtEntity> findAllByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<TeacherMotherLanguagePvtEntity> findFirstByTeacherMotherUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherMotherUUID, UUID languageUUID);

    Mono<TeacherMotherLanguagePvtEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);
}
