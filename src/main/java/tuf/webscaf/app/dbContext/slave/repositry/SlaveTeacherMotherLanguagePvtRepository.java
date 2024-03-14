package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherLanguagePvtEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherLanguagePvtRepository extends ReactiveCrudRepository<SlaveTeacherMotherLanguagePvtEntity, Long> {
    Mono<SlaveTeacherMotherLanguagePvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //This Query Prints All the Language UUIDs that are mapped for Teacher Mother
    @Query("SELECT string_agg(language_uuid::text, ',') " +
            "as campusUUID FROM teacher_mth_languages_pvt " +
            "WHERE teacher_mth_languages_pvt.deleted_at IS NULL " +
            "AND teacher_mth_languages_pvt.teacher_mother_uuid = :teacherMotherUUID")
    Mono<String> getAllMappedLanguageUUIDAgainstTeacherMother(UUID teacherMotherUUID);
}
