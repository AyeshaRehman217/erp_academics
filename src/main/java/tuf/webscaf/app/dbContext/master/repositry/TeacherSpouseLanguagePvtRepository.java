package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSpouseLanguagePvtRepository extends ReactiveCrudRepository<TeacherSpouseLanguagePvtEntity, Long> {

    Mono<TeacherSpouseLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherSpouseLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherSpouseLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<TeacherSpouseLanguagePvtEntity> findAllByTeacherSpouseUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID teacherSpouseUUID, List<UUID> languageUUID);

    Flux<TeacherSpouseLanguagePvtEntity> findAllByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<TeacherSpouseLanguagePvtEntity> findFirstByTeacherSpouseUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID, UUID languageUUID);

    Mono<TeacherSpouseLanguagePvtEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);
}
