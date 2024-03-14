package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentFatherRepository extends ReactiveCrudRepository<SlaveStudentFatherEntity, Long>, SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepository {
    Flux<SlaveStudentFatherEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentFatherEntity> findAllByDeletedAtIsNullAndStatus(Pageable pageable, Boolean status);

    Mono<SlaveStudentFatherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentFatherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentFatherEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<SlaveStudentFatherEntity> findFirstByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByDeletedAtIsNullAndStatus(Boolean status);

    @Query("select count(*) \n" +
            " from std_fathers \n" +
            " join std_fth_profiles on std_fathers.uuid = std_fth_profiles.std_father_uuid \n" +
            " where std_fathers.deleted_at IS NULL\n" +
            " AND std_fth_profiles.deleted_at IS NULL\n" +
            " and (std_fth_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_fth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentFatherStudentFatherProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_fathers \n" +
            " join std_fth_profiles on std_fathers.uuid = std_fth_profiles.std_father_uuid \n" +
            " where std_fathers.deleted_at IS NULL\n" +
            " AND std_fth_profiles.deleted_at IS NULL\n" +
            " and std_fathers.status = :status " +
            " and (std_fth_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_fth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentFatherStudentFatherProfileContactNoWithStatus(String name, String nic, Boolean status);


}
