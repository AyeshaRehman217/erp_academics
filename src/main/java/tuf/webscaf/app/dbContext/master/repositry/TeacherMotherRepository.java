package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherEntity;

import java.util.UUID;

@Repository
public interface TeacherMotherRepository extends ReactiveCrudRepository<TeacherMotherEntity, Long> {
    Mono<TeacherMotherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherMotherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherMotherEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherMotherEntity> findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID uuid);

    Mono<TeacherMotherEntity> findByUuidAndTeacherUUIDAndDeletedAtIsNull(UUID uuid, UUID teacherUUID);
}
