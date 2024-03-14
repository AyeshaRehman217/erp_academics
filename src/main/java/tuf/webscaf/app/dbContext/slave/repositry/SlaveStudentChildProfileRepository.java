package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentChildProfileRepository extends ReactiveCrudRepository<SlaveStudentChildProfileEntity, Long> {

    Mono<SlaveStudentChildProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentChildProfileEntity> findFirstByNicAndDeletedAtIsNull(String nic);

    Mono<SlaveStudentChildProfileEntity> findFirstByNicAndDeletedAtIsNullAndUuidIsNot(String nic, UUID uuid);

    Flux<SlaveStudentChildProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveStudentChildProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    //Find By Country uuid In Config Module
    Mono<SlaveStudentChildProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    Mono<SlaveStudentChildProfileEntity> findFirstByStudentChildUUIDAndDeletedAtIsNull(UUID studentChildUUID);

    //Find By State uuid In Config Module
    Mono<SlaveStudentChildProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentChildProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Student Child Profile Against Student UUID, Student Child UUID and
    @Query("SELECT std_child_profiles.* from std_child_profiles \n" +
            " join std_childs\n" +
            " on std_child_profiles.std_child_uuid = std_childs.uuid \n" +
            " join students \n" +
            " on std_childs.student_uuid = students.uuid \n" +
            " where std_child_profiles.uuid = :studentChildProfileUUID " +
            " and std_childs.uuid  = :studentChildUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_child_profiles.deleted_at is null \n" +
            " and std_childs.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentChildProfileEntity> showStudentChildProfileAgainstStudentAndStudentChild(UUID studentUUID, UUID studentChildUUID, UUID studentChildProfileUUID);

}
