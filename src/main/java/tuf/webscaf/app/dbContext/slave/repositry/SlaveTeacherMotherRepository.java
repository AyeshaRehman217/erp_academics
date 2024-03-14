package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherMotherRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherMotherRepository extends ReactiveCrudRepository<SlaveTeacherMotherEntity, Long>, SlaveCustomTeacherMotherRepository {
    Flux<SlaveTeacherMotherEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherMotherEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveTeacherMotherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherMotherEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    @Query("select count(*) \n" +
            " from teacher_mothers \n" +
            " join teacher_mth_profiles on teacher_mothers.uuid = teacher_mth_profiles.teacher_mother_uuid \n" +
            " where teacher_mothers.deleted_at IS NULL \n" +
            " AND teacher_mth_profiles.deleted_at IS NULL \n" +
            " and (teacher_mth_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_mth_profiles.email ILIKE concat('%',:email,'%') " +
            " or teacher_mth_profiles.nic ILIKE concat('%',:nic,'%') " +
            " or teacher_mth_profiles.official_tel ILIKE concat('%',:telephoneNo,'%'))")
    Mono<Long> countTeacherMotherTeacherMotherProfileContactNoWithOutStatus(String name, String email, String telephoneNo, String nic);

    @Query("select count(*) \n" +
            " from teacher_mothers \n" +
            " join teacher_mth_profiles on teacher_mothers.uuid = teacher_mth_profiles.teacher_mother_uuid \n" +
            " where teacher_mothers.deleted_at IS NULL \n" +
            " AND teacher_mth_profiles.deleted_at IS NULL \n" +
            " AND teacher_mothers.status =: status \n" +
            " and (teacher_mth_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_mth_profiles.email ILIKE concat('%',:email,'%') " +
            " or teacher_mth_profiles.nic ILIKE concat('%',:nic,'%') " +
            " or teacher_mth_profiles.official_tel ILIKE concat('%',:telephoneNo,'%'))")
    Mono<Long> countTeacherMotherTeacherMotherProfileContactNoWithStatus(Boolean status, String name, String email, String telephoneNo, String nic);
}
