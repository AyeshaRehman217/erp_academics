package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherProfileRepository extends ReactiveCrudRepository<SlaveStudentFatherProfileEntity, Long> {
    Flux<SlaveStudentFatherProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveStudentFatherProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveStudentFatherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentFatherProfileEntity> findFirstByStudentFatherUUIDAndDeletedAtIsNull(UUID studentFatherUUID);

    Mono<SlaveStudentFatherProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentFatherProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentFatherProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Student Father Profile Against Student UUID, Student Father UUID and
    @Query("SELECT std_fth_profiles.* from std_fth_profiles \n" +
            " join std_fathers\n" +
            " on std_fth_profiles.std_father_uuid = std_fathers.uuid \n" +
            " join students \n" +
            " on std_fathers.student_uuid = students.uuid \n" +
            " where std_fth_profiles.uuid = :studentFatherProfileUUID " +
            " and std_fathers.uuid  = :studentFatherUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_fth_profiles.deleted_at is null \n" +
            " and std_fathers.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentFatherProfileEntity> showStudentFatherProfileAgainstStudentAndStudentFather(UUID studentUUID, UUID studentFatherUUID, UUID studentFatherProfileUUID);
}
