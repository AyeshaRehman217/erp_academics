package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.CourseSubjectEntity;

import java.util.UUID;

@Repository
public interface CourseSubjectRepository extends ReactiveCrudRepository<CourseSubjectEntity, Long> {

    Mono<CourseSubjectEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<CourseSubjectEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<CourseSubjectEntity> findByUuidAndObeAndDeletedAtIsNull(UUID uuid, Boolean OBE);

    Mono<CourseSubjectEntity> findFirstByCourseUUIDAndSubjectUUIDAndDeletedAtIsNull(UUID courseUuid, UUID subjectUuid);

    Mono<CourseSubjectEntity> findFirstByCourseUUIDAndSubjectUUIDAndDeletedAtIsNullAndUuidIsNot(UUID courseUuid, UUID subjectUuid, UUID uuid);

//    Flux<CourseSubjectEntity> findAllByDeletedAtIsNull(Pageable pageable);
//
//    Mono<Long> countAllByDeletedAtIsNull();


}
