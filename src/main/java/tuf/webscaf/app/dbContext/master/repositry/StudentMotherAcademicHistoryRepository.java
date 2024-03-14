package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherAcademicHistoryEntity;

import java.util.UUID;

@Repository
public interface StudentMotherAcademicHistoryRepository extends ReactiveCrudRepository<StudentMotherAcademicHistoryEntity, Long> {
    Mono<StudentMotherAcademicHistoryEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentMotherAcademicHistoryEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndDeletedAtIsNull(UUID degreeUUID);

    Mono<StudentMotherAcademicHistoryEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUuid);

    Mono<StudentMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentMotherUUIDAndDeletedAtIsNull(UUID degreeUUID, UUID studentMotherUUID);

    Mono<StudentMotherAcademicHistoryEntity> findFirstByDegreeUUIDAndStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(UUID degreeUUID, UUID studentMotherUUID, UUID uuid);
}
