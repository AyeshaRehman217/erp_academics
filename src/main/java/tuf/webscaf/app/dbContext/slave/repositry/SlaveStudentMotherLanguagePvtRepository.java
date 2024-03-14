package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentMotherLanguagePvtEntity, Long> {
    Mono<SlaveStudentMotherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Mother
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_mth_languages_pvt " +
            "WHERE std_mth_languages_pvt.deleted_at IS NULL " +
            "AND std_mth_languages_pvt.std_mother_uuid = :studentMotherUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentMother(UUID studentMotherUUID);
}
