package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherEntity;

import java.util.UUID;

@Repository
public interface StudentMotherRepository extends ReactiveCrudRepository<StudentMotherEntity, Long> {
    Mono<StudentMotherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID mthUuid);

    Mono<StudentMotherEntity> findByUuidAndStudentUUIDAndDeletedAtIsNull(UUID uuid, UUID studentUUID);

    Mono<StudentMotherEntity> findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(UUID mthUuid, UUID uuid);


}
