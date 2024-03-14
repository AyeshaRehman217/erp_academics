package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.FacultyTeacherEntity;

import java.util.UUID;

@Repository
public interface FacultyTeacherRepository extends ReactiveCrudRepository<FacultyTeacherEntity, Long> {
    Mono<FacultyTeacherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<FacultyTeacherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<FacultyTeacherEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<FacultyTeacherEntity> findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID,UUID uuid);
}
