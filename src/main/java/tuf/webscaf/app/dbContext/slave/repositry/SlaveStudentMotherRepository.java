package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentMotherRepository extends ReactiveCrudRepository<SlaveStudentMotherEntity, Long>, SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepository {
    Flux<SlaveStudentMotherEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentMotherEntity> findAllByDeletedAtIsNullAndStatus(Pageable pageable, Boolean status);

    Mono<SlaveStudentMotherEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentMotherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentMotherEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<SlaveStudentMotherEntity> findFirstByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByDeletedAtIsNullAndStatus(Boolean status);

    @Query("select count(*) \n" +
            " from std_mothers \n" +
            " join std_mth_profiles on std_mothers.uuid = std_mth_profiles.std_mother_uuid \n" +
            " where std_mothers.deleted_at IS NULL\n" +
            " AND std_mth_profiles.deleted_at IS NULL\n" +
            " and (std_mth_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_mth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentMotherStudentMotherProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_mothers \n" +
            " join std_mth_profiles on std_mothers.uuid = std_mth_profiles.std_mother_uuid \n" +
            " where std_mothers.deleted_at IS NULL\n" +
            " AND std_mth_profiles.deleted_at IS NULL\n" +
            " and std_mothers.status = :status "+
            " and (std_mth_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_mth_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentMotherStudentMotherProfileContactNoWithStatus(String name, String nic, Boolean status);


}
