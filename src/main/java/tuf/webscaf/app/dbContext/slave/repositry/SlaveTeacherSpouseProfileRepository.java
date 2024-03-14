package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseProfileRepository extends ReactiveCrudRepository<SlaveTeacherSpouseProfileEntity, Long> {
    Flux<SlaveTeacherSpouseProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic, String officialTel);

    Flux<SlaveTeacherSpouseProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2, String officialTel, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic, String officialTel);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status1, String officialTel, Boolean status2);

    Mono<SlaveTeacherSpouseProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSpouseProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherSpouseProfileEntity> findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(UUID teacherSpouseUUID);

    Mono<SlaveTeacherSpouseProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUuid);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherSpouseProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherSpouseProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Spouse Profile Against Teacher UUID, Teacher Spouse UUID and
    @Query("SELECT teacher_spouse_profiles.* from teacher_spouse_profiles \n" +
            " join teacher_spouses \n" +
            " on teacher_spouse_profiles.teacher_spouse_uuid = teacher_spouses.uuid \n" +
            " join teachers \n" +
            " on teacher_spouses.teacher_uuid = teachers.uuid \n" +
            " where teacher_spouse_profiles.uuid = :teacherSpouseProfileUUID " +
            " and teacher_spouses.uuid  = :teacherSpouseUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_spouse_profiles.deleted_at is null \n" +
            " and teacher_spouses.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherSpouseProfileEntity> showTeacherSpouseProfileAgainstTeacherAndTeacherSpouse(UUID teacherUUID, UUID teacherSpouseUUID, UUID teacherSpouseProfileUUID);
}
