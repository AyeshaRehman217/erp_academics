package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherGuardianLanguagePvtEntity, Long> {
    Mono<SlaveTeacherGuardianLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Guardian
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_grd_languages_pvt " +
            "WHERE teacher_grd_languages_pvt.deleted_at IS NULL " +
            "AND teacher_grd_languages_pvt.teacher_guardian_uuid = :teacherGuardianUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherGuardian(UUID teacherGuardianUUID);
}
