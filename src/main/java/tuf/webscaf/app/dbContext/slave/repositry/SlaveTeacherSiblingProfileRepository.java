package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingProfileEntity;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingProfileRepository extends ReactiveCrudRepository<SlaveTeacherSiblingProfileEntity, Long> {
    Flux<SlaveTeacherSiblingProfileEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name, String nic);

    Flux<SlaveTeacherSiblingProfileEntity> findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String name, Boolean status, String nic, Boolean status2);

    Mono<Long> countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(String name, String nic);

    Mono<Long> countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String name, Boolean status, String nic, Boolean status2);

    Mono<SlaveTeacherSiblingProfileEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveTeacherSiblingProfileEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherSiblingProfileEntity> findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(UUID teacherSiblingUUID);

    //Find By Country uuid In Config Module
    Mono<SlaveTeacherSiblingProfileEntity> findFirstByCountryUUIDAndDeletedAtIsNull(UUID countryUUID);

    //Find By State uuid In Config Module
    Mono<SlaveTeacherSiblingProfileEntity> findFirstByStateUUIDAndDeletedAtIsNull(UUID stateUUID);

    //Find By City uuid In Config Module
    Mono<SlaveTeacherSiblingProfileEntity> findFirstByCityUUIDAndDeletedAtIsNull(UUID cityUUID);

    //show Teacher Sibling Profile Against Teacher UUID, Teacher Sibling UUID and
    @Query("SELECT teacher_sibling_profiles.* from teacher_sibling_profiles \n" +
            " join teacher_siblings \n" +
            " on teacher_sibling_profiles.teacher_sibling_uuid = teacher_siblings.uuid \n" +
            " join teachers \n" +
            " on teacher_siblings.teacher_uuid = teachers.uuid \n" +
            " where teacher_sibling_profiles.uuid = :teacherSiblingProfileUUID " +
            " and teacher_siblings.uuid  = :teacherSiblingUUID " +
            " and teachers.uuid  = :teacherUUID " +
            " and teacher_sibling_profiles.deleted_at is null \n" +
            " and teacher_siblings.deleted_at is null \n" +
            " and teachers.deleted_at is null")
    Mono<SlaveTeacherSiblingProfileEntity> showTeacherSiblingProfileAgainstTeacherAndTeacherSibling(UUID teacherUUID, UUID teacherSiblingUUID, UUID teacherSiblingProfileUUID);
}
