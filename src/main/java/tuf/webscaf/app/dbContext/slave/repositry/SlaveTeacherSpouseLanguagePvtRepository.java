package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherSpouseLanguagePvtEntity, Long> {
    Mono<SlaveTeacherSpouseLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Spouse
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_spouse_languages_pvt " +
            "WHERE teacher_spouse_languages_pvt.deleted_at IS NULL " +
            "AND teacher_spouse_languages_pvt.teacher_spouse_uuid = :teacherSpouseUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherSpouse(UUID teacherSpouseUUID);
}
