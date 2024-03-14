package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentChildRepository extends ReactiveCrudRepository<SlaveStudentChildEntity, Long>, SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepository {

    Mono<SlaveStudentChildEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveStudentChildEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Mono<SlaveStudentChildEntity> findFirstByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Flux<SlaveStudentChildEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveStudentChildEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveStudentChildEntity> findAllByStudentUUIDAndDeletedAtIsNull(Pageable pageable, UUID studentUUID);

    Flux<SlaveStudentChildEntity> findAllByStudentUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID studentUUID, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByStudentUUIDAndDeletedAtIsNull(UUID studentUUID);

    Mono<Long> countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID studentUUID, Boolean status);

    @Query("select count(*) \n" +
            " from std_childs \n" +
            " join std_child_profiles on std_childs.uuid = std_child_profiles.std_child_uuid \n" +
            " where std_childs.deleted_at IS NULL\n" +
            " AND std_child_profiles.deleted_at IS NULL\n" +
            " and (std_child_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_child_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentChildStudentChildProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from std_childs \n" +
            " join std_child_profiles on std_childs.uuid = std_child_profiles.std_child_uuid \n" +
            " where std_childs.deleted_at IS NULL\n" +
            " AND std_child_profiles.deleted_at IS NULL\n" +
            " and std_childs.status = :status " +
            " and (std_child_profiles.name ILIKE concat('%',:name,'%') " +
            " or std_child_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countStudentChildStudentChildProfileContactNoWithStatus(String name, String nic, Boolean status);
}
