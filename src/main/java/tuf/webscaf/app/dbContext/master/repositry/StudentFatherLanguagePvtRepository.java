package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentFatherLanguagePvtRepository extends ReactiveCrudRepository<StudentFatherLanguagePvtEntity, Long> {

    Mono<StudentFatherLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentFatherLanguagePvtEntity> findAllByStudentFatherUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentFatherUUID, List<UUID> languageUUID);

    Flux<StudentFatherLanguagePvtEntity> findAllByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);

    Mono<StudentFatherLanguagePvtEntity> findFirstByStudentFatherUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentFatherUUID, UUID languageUUID);

    Mono<StudentFatherLanguagePvtEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);
}
