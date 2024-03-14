package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherGuardianRepository extends ReactiveCrudRepository<SlaveTeacherGuardianEntity, Long>, SlaveCustomTeacherGuardianTeacherGuardianProfileContactNoFacadeRepository {
    Flux<SlaveTeacherGuardianEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherGuardianEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveTeacherGuardianEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherGuardianEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    @Query("select count(*) \n" +
            " from teacher_guardians \n" +
            " join teacher_grd_profiles on teacher_guardians.uuid = teacher_grd_profiles.teacher_guardian_uuid \n" +
            " where teacher_guardians.deleted_at IS NULL\n" +
            " AND teacher_grd_profiles.deleted_at IS NULL\n" +
            " and (teacher_grd_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_grd_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherGuardianTeacherGuardianProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from teacher_guardians \n" +
            " join teacher_grd_profiles on teacher_guardians.uuid = teacher_grd_profiles.teacher_guardian_uuid \n" +
            " where teacher_guardians.deleted_at IS NULL\n" +
            " AND teacher_grd_profiles.deleted_at IS NULL\n" +
            " and teacher_guardians.status = :status " +
            " and (teacher_grd_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_grd_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherGuardianTeacherGuardianProfileContactNoWithStatus(String name, String nic, Boolean status);

}
