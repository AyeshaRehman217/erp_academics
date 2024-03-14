package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherContactNoRepository extends ReactiveCrudRepository<TeacherContactNoEntity, Long> {
    Mono<TeacherContactNoEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<TeacherContactNoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<TeacherContactNoEntity> findAllByTeacherMetaUUIDAndDeletedAtIsNull(UUID teacherMetaUUID);

    Mono<TeacherContactNoEntity> findFirstByContactNoInAndTeacherMetaUUIDAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNull(List<String> contactNoList, UUID teacherMetaUUID, List<UUID> contactTypeUUIDList, UUID contactCategoryUUID);

    //Check if Meta Record Exist with the same Contact Type Category and Contact No (While Storing record)
    Mono<TeacherContactNoEntity> findFirstByContactNoAndTeacherMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNull(String contactNo, UUID TeacherMetaUUID, UUID contactTypeUUID, UUID contactCategoryUUID);

    //Check if Meta Record Exist with the same Contact Type Category and Contact No (While Updating record)
    Mono<TeacherContactNoEntity> findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(List<String> contactNo, List<UUID> contactTypeUUID, UUID contactCategoryUUID, UUID teacherMetaUUID);

    //Check if Meta Record Exist with the same Contact Type Category and Contact No (While Storing record) in Facade Handler
    Mono<TeacherContactNoEntity> findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(List<String> contactNo, List<UUID> contactTypeUUID, UUID contactCategoryUUID, UUID teacherMetaUUID);

    Mono<TeacherContactNoEntity> findFirstByContactNoAndTeacherMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNullAndUuidIsNot(String contactNo, UUID TeacherMetaUUID, UUID contactTypeUUID, UUID contactCategoryUUID, UUID teacherContactNoUUID);


}
