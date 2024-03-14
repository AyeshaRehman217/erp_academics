package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseProfileRepository extends ReactiveCrudRepository<SlaveStudentSpouseProfileEntity, Long> {
    Flux<SlaveStudentSpouseProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic, String officialTel);

    Flux<SlaveStudentSpouseProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2, String officialTel, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic, String officialTel);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status1, String officialTel, Boolean status2);

    Mono<SlaveStudentSpouseProfileEntity> findByIdAndDeletedAtIsNull(Long id);
    
    Mono<SlaveStudentSpouseProfileEntity> findFirstByStudentSpouseUUIDAndDeletedAtIsNull(UUID studentSpouseUUID);

    Mono<SlaveStudentSpouseProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSpouseProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveStudentSpouseProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveStudentSpouseProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Student Spouse Profile Against Student UUID, Student Spouse UUID and
    @Query("SELECT std_spouse_profiles.* from std_spouse_profiles \n" +
            " join std_spouses \n" +
            " on std_spouse_profiles.std_spouse_uuid = std_spouses.uuid \n" +
            " join students \n" +
            " on std_spouses.student_uuid = students.uuid \n" +
            " where std_spouse_profiles.uuid = :studentSpouseProfileUUID " +
            " and std_spouses.uuid  = :studentSpouseUUID " +
            " and students.uuid  = :studentUUID " +
            " and std_spouse_profiles.deleted_at is null \n" +
            " and std_spouses.deleted_at is null \n" +
            " and students.deleted_at is null")
    Mono<SlaveStudentSpouseProfileEntity> showStudentSpouseProfileAgainstStudentAndStudentSpouse(UUID studentUUID, UUID studentSpouseUUID, UUID studentSpouseProfileUUID);
}
