package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentSpouseLanguagePvtEntity, Long> {
    Mono<SlaveStudentSpouseLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Spouse
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_spouse_languages_pvt " +
            "WHERE std_spouse_languages_pvt.deleted_at IS NULL " +
            "AND std_spouse_languages_pvt.std_spouse_uuid = :studentSpouseUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentSpouse(UUID studentSpouseUUID);
}
