package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherChildTeacherChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherChildRepository extends ReactiveCrudRepository<SlaveTeacherChildEntity, Long> , SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepository {
    Flux<SlaveTeacherChildEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Flux<SlaveTeacherChildEntity> findAllByStatusAndDeletedAtIsNull(Pageable pageable, Boolean status);

    Flux<SlaveTeacherChildEntity> findAllByTeacherUUIDAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID);

    Flux<SlaveTeacherChildEntity> findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(Pageable pageable, UUID teacherUUID, Boolean status);

    Mono<Long> countByDeletedAtIsNull();

    Mono<Long> countByStatusAndDeletedAtIsNull(Boolean status);

    Mono<Long> countByTeacherUUIDAndDeletedAtIsNull(UUID teacherUUID);

    Mono<Long> countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID teacherUUID, Boolean status);

    Mono<SlaveTeacherChildEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("select count(*) \n" +
            " from teacher_childs \n" +
            " join teacher_child_profiles on teacher_childs.uuid = teacher_child_profiles.teacher_child_uuid \n" +
            " where teacher_childs.deleted_at IS NULL\n" +
            " AND teacher_child_profiles.deleted_at IS NULL\n" +
            " and (teacher_child_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_child_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherChildTeacherChildProfileContactNoWithOutStatus(String name, String nic);

    @Query("select count(*) \n" +
            " from teacher_childs \n" +
            " join teacher_child_profiles on teacher_childs.uuid = teacher_child_profiles.teacher_child_uuid \n" +
            " where teacher_childs.deleted_at IS NULL\n" +
            " AND teacher_child_profiles.deleted_at IS NULL\n" +
            " and teacher_childs.status = :status " +
            " and (teacher_child_profiles.name ILIKE concat('%',:name,'%') " +
            " or teacher_child_profiles.nic ILIKE concat('%',:nic,'%'))")
    Mono<Long> countTeacherChildTeacherChildProfileContactNoWithStatus(String name, String nic, Boolean status);

}
