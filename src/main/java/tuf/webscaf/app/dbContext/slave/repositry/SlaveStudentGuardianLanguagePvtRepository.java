package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentGuardianLanguagePvtEntity, Long> {
    Mono<SlaveStudentGuardianLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Guardian
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_grd_languages_pvt " +
            "WHERE std_grd_languages_pvt.deleted_at IS NULL " +
            "AND std_grd_languages_pvt.std_guardian_uuid = :studentGuardianUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentGuardian(UUID studentGuardianUUID);
}
