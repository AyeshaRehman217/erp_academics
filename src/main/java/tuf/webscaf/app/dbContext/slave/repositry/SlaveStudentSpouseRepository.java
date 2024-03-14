package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentSpouseRepository extends ReactiveCrudRepository<SlaveStudentSpouseEntity, Long>, SlaveCustomStudentSpouseStudentSpouseProfileContactNoFacadeRepository {
    Flux<SlaveStudentSpouseEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentSpouseEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveStudentSpouseEntity> findAllByStudentUUIDAndDeletedAtIsNull(Pageable pageable, UUID studentUUID);

    Flux<SlaveStudentSpouseEntity> findAllByStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID studentUUID, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<Long> countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID studentUUID, Boolean status);

    Mono<SlaveStudentSpouseEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSpouseEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    @Query("select count(*) \n" +
            " from std_spouses \n" +
            " join std_spouse_profiles on std_spouses.uuid = std_spouse_profiles.std_spouse_uuid \n" +
            " where std_spouses.deleted_at IS NULL\n" +
            " AND std_spouse_profiles.deleted_at IS NULL\n" +
            " and (std_spouse_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_spouse_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentSpouseStudentSpouseProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_spouses \n" +
            " join std_spouse_profiles on std_spouses.uuid = std_spouse_profiles.std_spouse_uuid \n" +
            " where std_spouses.deleted_at IS NULL\n" +
            " AND std_spouse_profiles.deleted_at IS NULL\n" +
            " and std_spouses.status = :status " +
            " and (std_spouse_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_spouse_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentSpouseStudentSpouseProfileContactNoWithStatus(String name, String nic, Boolean status);

}
