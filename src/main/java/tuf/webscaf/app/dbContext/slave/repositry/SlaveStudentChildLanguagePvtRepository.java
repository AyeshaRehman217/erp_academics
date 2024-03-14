package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentChildLanguagePvtEntity, Long> {
    Mono<SlaveStudentChildLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Child
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_child_languages_pvt " +
            "WHERE std_child_languages_pvt.deleted_at IS NULL " +
            "AND std_child_languages_pvt.std_child_uuid = :studentChildUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentChild(UUID studentChildUUID);
}
