package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentFatherLanguagePvtEntity, Long> {
    Mono<SlaveStudentFatherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student Father
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_fth_languages_pvt " +
            "WHERE std_fth_languages_pvt.deleted_at IS NULL " +
            "AND std_fth_languages_pvt.std_father_uuid = :studentFatherUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudentFather(UUID studentFatherUUID);
}
