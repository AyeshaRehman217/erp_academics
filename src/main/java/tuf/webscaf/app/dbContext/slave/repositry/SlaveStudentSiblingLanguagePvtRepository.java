package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentSiblingLanguagePvtEntity, Long> {
    Mono<SlaveStudentSiblingLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Sibling
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_sibling_languages_pvt " +
            "WHERE std_sibling_languages_pvt.deleted_at IS NULL " +
            "AND std_sibling_languages_pvt.std_sibling_uuid = :studentSiblingUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentSibling(UUID studentSiblingUUID);
}
