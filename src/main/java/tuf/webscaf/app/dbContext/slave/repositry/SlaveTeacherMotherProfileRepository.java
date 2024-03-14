package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherProfileRepository extends ReactiveCrudRepository<SlaveTeacherMotherProfileEntity, Long> {
    Flux<SlaveTeacherMotherProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic, String officialTel);

    Flux<SlaveTeacherMotherProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2, String officialTel, Boolean status3);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic, String officialTel);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status1, String officialTel, Boolean status2);

    Mono<SlaveTeacherMotherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherMotherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherMotherProfileEntity> findFirstByTeacherMotherUUIDAndDeletedAtIsNull(UUID teacherMotherUUID);

    Mono<SlaveTeacherMotherProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherMotherProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherMotherProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Mother Profile Against Teacher UUID, Teacher Mother UUID and
    @Query("SELECT teacher_mth_profiles.* from teacher_mth_profiles \n" +
            " join teacher_mothers\n" +
            " on teacher_mth_profiles.teacher_mother_uuid = teacher_mothers.uuid \n" +
            " join teachers \n" +
            " on teacher_mothers.teacher_uuid = teachers.uuid \n" +
            " where teacher_mth_profiles.uuid = :teacherMotherProfileUUID " +
            " and teacher_mothers.uuid  = :teacherMotherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_mth_profiles.deleted_at is null \n" +
            " and teacher_mothers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherMotherProfileEntity> showTeacherMotherProfileAgainstTeacherAndTeacherMother(UUID teacherUUID, UUID teacherMotherUUID, UUID teacherMotherProfileUUID);
}
