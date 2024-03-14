package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherProfileRepository extends ReactiveCrudRepository<SlaveStudentMotherProfileEntity, Long> {

    Flux<SlaveStudentMotherProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveStudentMotherProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveStudentMotherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentMotherProfileEntity> findFirstByStudentMotherUUIDAndDeletedAtIsNull(UUID studentMotherUUID);

    Mono<SlaveStudentMotherProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentMotherProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentMotherProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Student Mother Profile Against Student UUID, Student Mother UUID and
    @Query("SELECT std_mth_profiles.* from std_mth_profiles \n" +
            " join std_mothers\n" +
            " on std_mth_profiles.std_mother_uuid = std_mothers.uuid \n" +
            " join students \n" +
            " on std_mothers.student_uuid = students.uuid \n" +
            " where std_mth_profiles.uuid = :studentMotherProfileUUID " +
            " and std_mothers.uuid  = :studentMotherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_mth_profiles.deleted_at is null \n" +
            " and std_mothers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentMotherProfileEntity> showStudentMotherProfileAgainstStudentAndStudentMother(UUID studentUUID, UUID studentMotherUUID, UUID studentMotherProfileUUID);
}
