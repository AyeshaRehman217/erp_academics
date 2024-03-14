package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianProfileRepository extends ReactiveCrudRepository<SlaveTeacherGuardianProfileEntity, Long> {
    Flux<SlaveTeacherGuardianProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic, String officialTel);

    Flux<SlaveTeacherGuardianProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2, String officialTel, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic, String officialTel);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status1, String officialTel, Boolean status2);

    Mono<SlaveTeacherGuardianProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherGuardianProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherGuardianProfileEntity> findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(UUID teacherGuardianUUID);

    Mono<SlaveTeacherGuardianProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherGuardianProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherGuardianProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Guardian Profile Against Teacher UUID, Teacher Guardian UUID and
    @Query("SELECT teacher_grd_profiles.* from teacher_grd_profiles \n" +
            " join teacher_guardians \n" +
            " on teacher_grd_profiles.teacher_guardian_uuid = teacher_guardians.uuid \n" +
            " join teachers \n" +
            " on teacher_guardians.teacher_uuid = teachers.uuid \n" +
            " where teacher_grd_profiles.uuid = :teacherGuardianProfileUUID " +
            " and teacher_guardians.uuid  = :teacherGuardianUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_grd_profiles.deleted_at is null \n" +
            " and teacher_guardians.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherGuardianProfileEntity> showTeacherGuardianProfileAgainstTeacherAndTeacherGuardian(UUID teacherUUID, UUID teacherGuardianUUID, UUID teacherGuardianProfileUUID);
}
