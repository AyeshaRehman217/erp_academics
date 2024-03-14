package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherSpouseRepository extends ReactiveCrudRepository<SlaveTeacherSpouseEntity, Long>, SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepository {
    Flux<SlaveTeacherSpouseEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherSpouseEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveTeacherSpouseEntity> findAllByTeacherUUIDAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID);

    Flux<SlaveTeacherSpouseEntity> findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<Long> countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID teacherUUID, Boolean status);

    Mono<SlaveTeacherSpouseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherSpouseEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    @Query("select count(*) \n" +
            " from teacher_spouses \n" +
            " join teacher_spouse_profiles on teacher_spouses.uuid = teacher_spouse_profiles.teacher_spouse_uuid \n" +
            " where teacher_spouses.deleted_at IS NULL\n" +
            " AND teacher_spouse_profiles.deleted_at IS NULL\n" +
            " and (teacher_spouse_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_spouse_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherSpouseTeacherSpouseProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from teacher_spouses \n" +
            " join teacher_spouse_profiles on teacher_spouses.uuid = teacher_spouse_profiles.teacher_spouse_uuid \n" +
            " where teacher_spouses.deleted_at IS NULL\n" +
            " AND teacher_spouse_profiles.deleted_at IS NULL\n" +
            " and teacher_spouses.status = :status " +
            " and (teacher_spouse_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_spouse_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherSpouseTeacherSpouseProfileContactNoWithStatus(String name, String nic, Boolean status);

}
