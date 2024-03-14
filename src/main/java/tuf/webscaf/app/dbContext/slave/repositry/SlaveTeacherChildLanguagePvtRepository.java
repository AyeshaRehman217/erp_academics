package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherChildLanguagePvtEntity, Long> {
    Mono<SlaveTeacherChildLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Child
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_child_languages_pvt " +
            "WHERE teacher_child_languages_pvt.deleted_at IS NULL " +
            "AND teacher_child_languages_pvt.teacher_child_uuid = :teacherChildUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherChild(UUID teacherChildUUID);
}
