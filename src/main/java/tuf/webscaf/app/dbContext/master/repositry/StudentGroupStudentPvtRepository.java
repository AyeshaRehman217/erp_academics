package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentGroupStudentPvtEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGroupStudentPvtRepository extends ReactiveCrudRepository<StudentGroupStudentPvtEntity, Long> {

    Mono<StudentGroupStudentPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentGroupStudentPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentGroupStudentPvtEntity> findAllByStudentGroupUUIDAndStudentUUIDAndDeletedAtIsNull(UUID studentGroupUUID, UUID studentUUID);

    Flux<StudentGroupStudentPvtEntity> findAllByStudentGroupUUIDAndStudentUUIDInAndDeletedAtIsNull(UUID studentGroupUUID, List<UUID> studentUUID);

    Flux<StudentGroupStudentPvtEntity> findAllByStudentGroupUUIDAndDeletedAtIsNull(UUID studentGroupUUID);

    Mono<StudentGroupStudentPvtEntity> findFirstByStudentGroupUUIDAndStudentUUIDAndDeletedAtIsNull(UUID studentGroupUUID, UUID studentUUID);

    Mono<StudentGroupStudentPvtEntity> findFirstByStudentGroupUUIDAndDeletedAtIsNull(UUID studentGroupUUID);

    Mono<Long> countByStudentGroupUUIDAndDeletedAtIsNull(UUID studentGroupUUID);
}
