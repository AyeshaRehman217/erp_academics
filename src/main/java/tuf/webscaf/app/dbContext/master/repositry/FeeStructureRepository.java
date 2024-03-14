package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.FeeStructureEntity;

import java.util.UUID;

@Repository
public interface FeeStructureRepository extends ReactiveCrudRepository<FeeStructureEntity, Long> {
    Mono<FeeStructureEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<FeeStructureEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
    //Check If Academic Sessions Id Exists
    Mono<FeeStructureEntity> findFirstByAcademicSessionUUIDAndDeletedAtIsNull(UUID academicSessionUUID);

    //Check if Campus Id Exists in Fee Structures
    Mono<FeeStructureEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    Mono<FeeStructureEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);

    Mono<FeeStructureEntity> findFirstBySemesterUUIDAndDeletedAtIsNull(UUID semesterUUID);
}
