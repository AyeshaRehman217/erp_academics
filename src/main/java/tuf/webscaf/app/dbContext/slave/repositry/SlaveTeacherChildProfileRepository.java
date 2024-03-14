package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildProfileRepository extends ReactiveCrudRepository<SlaveTeacherChildProfileEntity, Long> {

    Flux<SlaveTeacherChildProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveTeacherChildProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveTeacherChildProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherChildProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherChildProfileEntity> findFirstByTeacherChildUUIDAndDeletedAtIsNull(UUID teacherChildUUID);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherChildProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherChildProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherChildProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Child Profile Against Teacher UUID, Teacher Child UUID and
    @Query("SELECT teacher_child_profiles.* from teacher_child_profiles \n" +
            " join teacher_childs\n" +
            " on teacher_child_profiles.teacher_child_uuid = teacher_childs.uuid \n" +
            " join teachers \n" +
            " on teacher_childs.teacher_uuid = teachers.uuid \n" +
            " where teacher_child_profiles.uuid = :teacherChildProfileUUID " +
            " and teacher_childs.uuid  = :teacherChildUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_child_profiles.deleted_at is null \n" +
            " and teacher_childs.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherChildProfileEntity> showTeacherChildProfileAgainstTeacherAndTeacherChild(UUID teacherUUID, UUID teacherChildUUID, UUID teacherChildProfileUUID);
}
