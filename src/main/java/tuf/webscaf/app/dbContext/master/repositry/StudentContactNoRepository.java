package tuf.webscaf.app.dbContext.master.repositry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentContactNoRepository extends ReactiveCrudRepository<StudentContactNoEntity, Long> {
    Mono<StudentContactNoEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<StudentContactNoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<StudentContactNoEntity> findFirstByContactTypeUUIDAndDeletedAtIsNull(UUID contactTypeUUid);

    Flux<StudentContactNoEntity> findAllByStudentMetaUUIDAndDeletedAtIsNull(UUID stdMetaUUID);

    Mono<StudentContactNoEntity> findFirstByStudentMetaUUIDAndDeletedAtIsNull(UUID studentMetaUUid);

    //Check if Meta Record Exist with the same Contact Type Category and Contact No (While Storing record)
    Mono<StudentContactNoEntity> findFirstByContactNoAndStudentMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNull(String contactNo, UUID studentMetaUUID, UUID contactTypeUUID, UUID contactCategoryUUID);

    //Check if Meta Record Exist with the same Contact Type Category and Contact No (While Updating record)
    Mono<StudentContactNoEntity> findFirstByContactNoAndStudentMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNullAndUuidIsNot(String contactNo, UUID studentMetaUUID, UUID contactTypeUUID, UUID contactCategoryUUID, UUID studentUUID);

    Mono<StudentContactNoEntity> findFirstByContactNoInAndStudentMetaUUIDAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNull(List<String> contactNo, UUID studentMetaUUID, List<UUID> contactTypeUUID, UUID contactCategoryUUID);

    //check if Contact No is Unique against contact type (Used by Update Function)
    Mono<StudentContactNoEntity> findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(List<String> contactNo, List<UUID> contactTypeUUID, UUID contactCategoryUUID, UUID studentMetaUUID);

    Mono<StudentContactNoEntity> findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(List<String> contactNoList, List<UUID> contactTypeUUID, UUID contactCategoryUUID, UUID studentMetaUUID);
}
