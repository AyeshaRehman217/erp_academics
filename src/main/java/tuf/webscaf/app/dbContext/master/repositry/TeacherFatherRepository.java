package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherEntity;

import java.util.UUID;

@Repository
public interface TeacherFatherRepository extends ReactiveCrudRepository<TeacherFatherEntity, Long> {
    Mono<TeacherFatherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherFatherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<TeacherFatherEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<TeacherFatherEntity> findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID teacherUUID, UUID uuid);

    Mono<TeacherFatherEntity> findByUuidAndTeacherUUIDAndDeletedAtIsNull(UUID uuid, UUID teacherUUID);
}
