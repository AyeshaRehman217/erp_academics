package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherSiblingRepository extends ReactiveCrudRepository<SlaveTeacherSiblingEntity, Long>, SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepository {
    Flux<SlaveTeacherSiblingEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherSiblingEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveTeacherSiblingEntity> findAllByTeacherUUIDAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID);

    Flux<SlaveTeacherSiblingEntity> findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<Long> countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID teacherUUID, Boolean status);

    Mono<SlaveTeacherSiblingEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("select count(*) \n" +
            " from teacher_siblings \n" +
            " join teacher_sibling_profiles on teacher_siblings.uuid = teacher_sibling_profiles.teacher_sibling_uuid \n" +
            " where teacher_siblings.deleted_at IS NULL\n" +
            " AND teacher_sibling_profiles.deleted_at IS NULL\n" +
            " and (teacher_sibling_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_sibling_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherSiblingTeacherSiblingProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from teacher_siblings \n" +
            " join teacher_sibling_profiles on teacher_siblings.uuid = teacher_sibling_profiles.teacher_sibling_uuid \n" +
            " where teacher_siblings.deleted_at IS NULL\n" +
            " AND teacher_sibling_profiles.deleted_at IS NULL\n" +
            " and teacher_siblings.status = :status " +
            " and (teacher_sibling_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_sibling_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherSiblingTeacherSiblingProfileContactNoWithStatus(String name, String nic, Boolean status);

}
