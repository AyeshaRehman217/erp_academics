package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<StudentEntity, Long> {
    Mono<StudentEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentEntity> findFirstByStudentIdAndDeletedAtIsNull(String id);

    Mono<StudentEntity> findFirstByStudentIdAndDeletedAtIsNullAndUuidIsNot(String id, UUID uuid);

    Flux<StudentEntity> findAllByUuidInAndDeletedAtIsNull(List<UUID> ids);

    Mono<StudentEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<StudentEntity> findFirstByOfficialEmailAndDeletedAtIsNull(String officialEmail);

}
