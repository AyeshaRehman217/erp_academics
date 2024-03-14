package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeMapper;

import java.util.UUID;

@Repository
public interface SlaveTeacherFatherRepository extends ReactiveCrudRepository<SlaveTeacherFatherEntity, Long>, SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepository {
    Flux<SlaveTeacherFatherEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherFatherEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveTeacherFatherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveTeacherFatherEntity> findFirstByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    @Query("select count(*) \n" +
            " from teacher_fathers \n" +
            " join teacher_fth_profiles on teacher_fathers.uuid = teacher_fth_profiles.teacher_father_uuid \n" +
            " where teacher_fathers.deleted_at IS NULL\n" +
            " AND teacher_fth_profiles.deleted_at IS NULL\n" +
            " and (teacher_fth_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_fth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherFatherTeacherFatherProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from teacher_fathers \n" +
            " join teacher_fth_profiles on teacher_fathers.uuid = teacher_fth_profiles.teacher_father_uuid \n" +
            " where teacher_fathers.deleted_at IS NULL\n" +
            " AND teacher_fth_profiles.deleted_at IS NULL\n" +
            " and teacher_fathers.status = :status " +
            " and (teacher_fth_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_fth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherFatherTeacherFatherProfileContactNoWithStatus(String name, String nic, Boolean status);
}
