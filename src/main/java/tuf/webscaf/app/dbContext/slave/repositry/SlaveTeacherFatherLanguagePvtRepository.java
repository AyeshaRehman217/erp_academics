package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherFatherLanguagePvtEntity, Long> {
    Mono<SlaveTeacherFatherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Father
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_fth_languages_pvt " +
            "WHERE teacher_fth_languages_pvt.deleted_at IS NULL " +
            "AND teacher_fth_languages_pvt.teacher_father_uuid = :teacherFatherUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherFather(UUID teacherFatherUUID);
}
