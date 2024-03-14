package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSpouseLanguagePvtRepository extends ReactiveCrudRepository<StudentSpouseLanguagePvtEntity, Long> {

    Mono<StudentSpouseLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentSpouseLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentSpouseLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentSpouseLanguagePvtEntity> findAllByStudentSpouseUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentSpouseUUID, List<UUID> languageUUID);

    Flux<StudentSpouseLanguagePvtEntity> findAllByStudentSpouseUUIDAndDeletedAtIsNull(UUID studentSpouseUUID);

    Mono<StudentSpouseLanguagePvtEntity> findFirstByStudentSpouseUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentSpouseUUID, UUID languageUUID);

    Mono<StudentSpouseLanguagePvtEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID studentSpouseUUID);
}
