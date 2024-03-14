package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherProfileRepository extends ReactiveCrudRepository<SlaveTeacherFatherProfileEntity, Long> {
    Flux<SlaveTeacherFatherProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveTeacherFatherProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveTeacherFatherProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherFatherProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherFatherProfileEntity> findFirstByTeacherFatherUUIDAndDeletedAtIsNull(UUID teacherFatherUUID);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherFatherProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherFatherProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherFatherProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Father Profile Against Teacher UUID, Teacher Father UUID and
    @Query("SELECT teacher_fth_profiles.* from teacher_fth_profiles \n" +
            " join teacher_fathers\n" +
            " on teacher_fth_profiles.teacher_father_uuid = teacher_fathers.uuid \n" +
            " join teachers \n" +
            " on teacher_fathers.teacher_uuid = teachers.uuid \n" +
            " where teacher_fth_profiles.uuid = :teacherFatherProfileUUID " +
            " and teacher_fathers.uuid  = :teacherFatherUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_fth_profiles.deleted_at is null \n" +
            " and teacher_fathers.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherFatherProfileEntity> showTeacherFatherProfileAgainstTeacherAndTeacherFather(UUID teacherUUID, UUID teacherFatherUUID, UUID teacherFatherProfileUUID);
}
