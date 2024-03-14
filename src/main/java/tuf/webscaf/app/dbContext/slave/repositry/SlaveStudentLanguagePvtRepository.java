package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentLanguagePvtRepository extends ReactiveCrudRepository<SlaveStudentLanguagePvtEntity, Long> {
    Mono<SlaveStudentLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Student
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM std_languages_pvt " +
            "WHERE std_languages_pvt.deleted_at IS NULL " +
            "AND std_languages_pvt.student_uuid = :studentUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstStudent(UUID studentUUID);
}
