package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherEntity;

import java.util.UUID;

@Repository
public interface StudentFatherRepository extends ReactiveCrudRepository<StudentFatherEntity, Long> {
    Mono<StudentFatherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentFatherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentFatherEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID mthUuid);

    Mono<StudentFatherEntity> findByUuidAndStudentUUIDAndDeletedAtIsNull(UUID uuid, UUID studentUUID);

    Mono<StudentFatherEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID mthUuid, UUID uuid);


}
