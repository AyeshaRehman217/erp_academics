package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherLanguagePvtEntity, Long> {
    Mono<SlaveTeacherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_languages_pvt " +
            "WHERE teacher_languages_pvt.deleted_at IS NULL " +
            "AND teacher_languages_pvt.teacher_uuid = :teacherUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacher(UUID teacherUUID);
}
