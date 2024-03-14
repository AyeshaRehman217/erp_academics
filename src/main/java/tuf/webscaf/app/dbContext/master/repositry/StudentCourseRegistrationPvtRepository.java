package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentCourseRegistrationPvtEntity;

import java.util.UUID;

@Repository
public interface StudentCourseRegistrationPvtRepository extends ReactiveCrudRepository<StudentCourseRegistrationPvtEntity, Long> {
    Mono<StudentCourseRegistrationPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentCourseRegistrationPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentCourseRegistrationPvtEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<StudentCourseRegistrationPvtEntity> findFirstByStudentUUIDAndCourseOfferedUUIDAndDeletedAtIsNull(UUID studentUUID,UUID courseOfferedUUID);

    Mono<StudentCourseRegistrationPvtEntity> findFirstByCourseOfferedUUIDAndStudentUUIDAndDeletedAtIsNull(UUID courseOfferedUUID, UUID studentUUID);

    Mono<StudentCourseRegistrationPvtEntity> findFirstByCourseOfferedUUIDAndStudentUUIDIsNot(UUID courseOfferedUUID, UUID studentUUID);

    Flux<StudentCourseRegistrationPvtEntity> findAllByCourseOfferedUUIDAndDeletedAtIsNull(UUID courseOfferedUUID);
}
