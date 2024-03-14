package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherSiblingLanguagePvtEntity, Long> {
    Mono<SlaveTeacherSiblingLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Sibling
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_sibling_languages_pvt " +
            "WHERE teacher_sibling_languages_pvt.deleted_at IS NULL " +
            "AND teacher_sibling_languages_pvt.teacher_sibling_uuid = :teacherSiblingUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherSibling(UUID teacherSiblingUUID);
}
