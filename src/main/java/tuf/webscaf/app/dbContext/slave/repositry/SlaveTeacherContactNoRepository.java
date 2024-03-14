package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomTeacherContactNoRepository;

import java.util.UUID;

@Repository
public interface SlaveTeacherContactNoRepository extends ReactiveCrudRepository<SlaveTeacherContactNoEntity, Long>, SlaveCustomTeacherContactNoRepository {
    Mono<SlaveTeacherContactNoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveTeacherContactNoEntity> findAllByTeacherMetaUUIDAndDeletedAtIsNull(UUID teacherMetaUUID);

    Flux<SlaveTeacherContactNoEntity> findAllByContactNoContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String contactNo);

    Flux<SlaveTeacherContactNoEntity> findAllByContactNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String contactNo, Boolean status);

    Mono<Long> countByContactNoContainingIgnoreCaseAndDeletedAtIsNull(String contactNo);

    Mono<Long> countByContactNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String contactNo, Boolean status);

    Mono<SlaveTeacherContactNoEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Index All Teacher Contact No. With and Without Status and Teacher Meta UUID filter
     **/
    @Query("select count(*) \n" +
            " from teacher_contact_nos \n" +
            " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where teacher_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and  teacher_contact_nos.teacher_meta_uuid= :teacherMetaUUID " +
            " AND teacher_contact_nos.status = :status " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or teacher_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countTeacherContactNoRecordWithStatus(UUID teacherMetaUUID, Boolean status, String key, String contactNo);

    @Query("select count(*) \n" +
            " from teacher_contact_nos \n" +
            " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where teacher_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and  teacher_contact_nos.teacher_meta_uuid= :teacherMetaUUID " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or teacher_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countTeacherContactNoRecordWithoutStatus(UUID teacherMetaUUID, String key, String contactNo);

    /**
     * Fetch All Records With and Without Status Filter
     **/
    @Query("select count(*) \n" +
            " from teacher_contact_nos \n" +
            " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where teacher_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " AND teacher_contact_nos.status = :status " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or teacher_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countAllRecordWithStatus(Boolean status, String key, String contactNo);

    /**
     * Fetch All Records Without Status Filter
     **/
    @Query("select count(*) \n" +
            " from teacher_contact_nos \n" +
            " left join contact_categories on teacher_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on teacher_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where teacher_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and (concat_ws('|',contact_categories.name,contact_types.name,teacher_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or teacher_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countAllRecordWithoutStatus(String key, String contactNo);

}
