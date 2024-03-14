package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepository;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contractImpl.SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepositoryImpl;
import tuf.webscaf.app.dbContext.slave.repositry.custom.mapper.SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeMapper;

import java.util.UUID;

@Repository
public interface SlaveStudentGuardianRepository extends ReactiveCrudRepository<SlaveStudentGuardianEntity, Long>, SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepository {
    Mono<SlaveStudentGuardianEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentGuardianEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentGuardianEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<SlaveStudentGuardianEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentGuardianEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    @Query("select count(*) \n" +
            " from std_guardians \n" +
            " join std_grd_profiles on std_guardians.uuid = std_grd_profiles.std_guardian_uuid \n" +
            " where std_guardians.deleted_at IS NULL\n" +
            " AND std_grd_profiles.deleted_at IS NULL\n" +
            " and (std_grd_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_grd_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentGuardianStudentGuardianProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_guardians \n" +
            " join std_grd_profiles on std_guardians.uuid = std_grd_profiles.std_guardian_uuid \n" +
            " where std_guardians.deleted_at IS NULL\n" +
            " AND std_grd_profiles.deleted_at IS NULL\n" +
            " and std_guardians.status = :status " +
            " and (std_grd_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_grd_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentGuardianStudentGuardianProfileContactNoWithStatus(String name, String nic, Boolean status);
}
