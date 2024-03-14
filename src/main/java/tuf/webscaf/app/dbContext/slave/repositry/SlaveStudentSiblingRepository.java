package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaveStudentSiblingRepository extends ReactiveCrudRepository<SlaveStudentSiblingEntity, Long>, SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepository {
    Flux<SlaveStudentSiblingEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentSiblingEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveStudentSiblingEntity> findAllByStudentUUIDAndDeletedAtIsNull(Pageable pageable, UUID studentUUID);

    Flux<SlaveStudentSiblingEntity> findAllByStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID studentUUID, Boolean status);

    Mono<SlaveStudentSiblingEntity> findByIdAndDeletedAtIsNull(Long id);

    Flux<SlaveStudentSiblingEntity> findAllByStudentUUIDContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(Pageable pageable, UUID stdUuid, List<UUID> uuid);

    Flux<SlaveStudentSiblingEntity> findAllByStudentUUIDContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(Pageable pageable, UUID stdUuid, Boolean status, List<UUID> uuid);

    Mono<Long> countByStudentUUIDContainingIgnoreCaseAndDeletedAtIsNullAndUuidIn(UUID stdUuid, List<UUID> uuid);

    Mono<Long> countByStudentUUIDContainingIgnoreCaseAndStatusAndDeletedAtIsNullAndUuidIn(UUID stdUuid, Boolean status, List<UUID> uuid);

    Mono<SlaveStudentSiblingEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentSiblingEntity> findFirstByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<Long> countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID studentUUID, Boolean status);

    @Query("select count(*) \n" +
            " from std_siblings \n" +
            " join std_sibling_profiles on std_siblings.uuid = std_sibling_profiles.std_sibling_uuid \n" +
            " where std_siblings.deleted_at IS NULL\n" +
            " AND std_sibling_profiles.deleted_at IS NULL\n" +
            " and (std_sibling_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_sibling_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentSiblingStudentSiblingProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_siblings \n" +
            " join std_sibling_profiles on std_siblings.uuid = std_sibling_profiles.std_sibling_uuid \n" +
            " where std_siblings.deleted_at IS NULL\n" +
            " AND std_sibling_profiles.deleted_at IS NULL\n" +
            " and std_siblings.status = :status " +
            " and (std_sibling_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_sibling_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentSiblingStudentSiblingProfileContactNoWithStatus(String name, String nic, Boolean status);

}
