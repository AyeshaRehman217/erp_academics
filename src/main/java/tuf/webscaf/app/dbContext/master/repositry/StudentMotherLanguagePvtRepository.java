package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherLanguagePvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentMotherLanguagePvtRepository extends ReactiveCrudRepository<StudentMotherLanguagePvtEntity, Long> {

    Mono<StudentMotherLanguagePvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherLanguagePvtEntity> findFirstByLanguageUUIDAndDeletedAtIsNull(UUID languageUUID);

    Flux<StudentMotherLanguagePvtEntity> findAllByStudentMotherUUIDAndLanguageUUIDInAndDeletedAtIsNull(UUID studentMotherUUID, List<UUID> languageUUID);

    Flux<StudentMotherLanguagePvtEntity> findAllByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);

    Mono<StudentMotherLanguagePvtEntity> findFirstByStudentMotherUUIDAndLanguageUUIDAndDeletedAtIsNull(UUID studentMotherUUID, UUID languageUUID);

    Mono<StudentMotherLanguagePvtEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);
}
