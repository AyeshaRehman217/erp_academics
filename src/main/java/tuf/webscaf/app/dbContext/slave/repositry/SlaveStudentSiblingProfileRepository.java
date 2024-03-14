package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSiblingProfileRepository extends ReactiveCrudRepository<SlaveStudentSiblingProfileEntity, Long> {
    Flux<SlaveStudentSiblingProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveStudentSiblingProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveStudentSiblingProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentSiblingProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSiblingProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentSiblingProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentSiblingProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    Mono<SlaveStudentSiblingProfileEntity> findFirstByStudentSiblingUUIDAndDeletedAtIsNull(UUID studentSiblingUUID);

    //show Student Sibling Profile Against Student UUID, Student Sibling UUID and
    @Query("SELECT std_sibling_profiles.* from std_sibling_profiles \n" +
            " join std_siblings\n" +
            " on std_sibling_profiles.std_sibling_uuid = std_siblings.uuid \n" +
            " join students \n" +
            " on std_siblings.student_uuid = students.uuid \n" +
            " where std_sibling_profiles.uuid = :studentSiblingProfileUUID " +
            " and std_siblings.uuid  = :studentSiblingUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_sibling_profiles.deleted_at is null \n" +
            " and std_siblings.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSiblingProfileEntity> showStudentSiblingProfileAgainstStudentAndStudentSibling(UUID studentUUID, UUID studentSiblingUUID, UUID studentSiblingProfileUUID);
}
