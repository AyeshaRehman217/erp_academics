package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.EnrollmentEntity;
import tuf.webscaf.app.dbContext.master.entity.SectionEntity;

import java.util.UUID;

@Repository
public interface SectionRepository extends ReactiveCrudRepository<SectionEntity, Long> {

    Mono<SectionEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SectionEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    //Check if Campus Id Exists in Sections
//    Mono<SectionEntity> findFirstByCampusUUIDAndDeletedAtIsNull(UUID campusUUID);

    //Check if Name Already Exists in Store Function
    Mono<SectionEntity> findFirstByNameIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull(String name, UUID courseOfferedUUID);

    //Check if Name Already Exists in Update Function
    Mono<SectionEntity> findFirstByNameIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(String name, UUID courseOfferedUUID, UUID uuid);

//    Mono<SectionEntity> findFirstByCourseUUIDAndDeletedAtIsNull(UUID courseUUID);
//
//    Mono<SectionEntity> findFirstBySemesterUUIDAndDeletedAtIsNull(UUID semesterUUID);
}
