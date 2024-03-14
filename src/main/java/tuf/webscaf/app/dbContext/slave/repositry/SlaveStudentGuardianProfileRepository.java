package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianProfileRepository extends ReactiveCrudRepository<SlaveStudentGuardianProfileEntity, Long> {
    Mono<SlaveStudentGuardianProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentGuardianProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveStudentGuardianProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveStudentGuardianProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentGuardianProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentGuardianProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentGuardianProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    Mono<SlaveStudentGuardianProfileEntity> findFirstByStudentGuardianUUIDAndDeletedAtIsNull(UUID studentGuardianUUID);

    //show Student Guardian Profile Against Student UUID, Student Guardian UUID
    @Query("SELECT std_grd_profiles.* from std_grd_profiles \n" +
            " join std_guardians \n" +
            " on std_grd_profiles.std_guardian_uuid = std_guardians.uuid \n" +
            " join students \n" +
            " on std_guardians.student_uuid = students.uuid \n" +
            " where std_grd_profiles.uuid = :studentGuardianProfileUUID " +
            " and std_guardians.uuid  = :studentGuardianUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_grd_profiles.deleted_at is null \n" +
            " and std_guardians.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentGuardianProfileEntity> showStudentGuardianProfileAgainstStudentAndStudentGuardian(UUID studentUUID, UUID studentGuardianUUID, UUID studentGuardianProfileUUID);
}
