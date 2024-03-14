package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomStudentContactNoRepository;

import java.util.UUID;

@Repository
public interface SlaveStudentContactNoRepository extends ReactiveCrudRepository<SlaveStudentContactNoEntity, Long>, SlaveCustomStudentContactNoRepository {
    Mono<SlaveStudentContactNoEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Flux<SlaveStudentContactNoEntity> findAllByContactNoContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String contactNo);

    Flux<SlaveStudentContactNoEntity> findAllByContactNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(Pageable pageable, String contactNo, Boolean status);

    Mono<Long> countByContactNoContainingIgnoreCaseAndDeletedAtIsNull(String contactNo);

    Mono<Long> countByContactNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(String contactNo, Boolean status);

    Flux<SlaveStudentContactNoEntity> findAllByStudentMetaUUIDAndDeletedAtIsNull(UUID stdMetaUUID);

    Mono<SlaveStudentContactNoEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Index All Student Contact No. With and Without Status and Student Meta UUID filter
     **/
    @Query("select count(*) \n" +
            " from student_contact_nos \n" +
            " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where student_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and  student_contact_nos.student_meta_uuid= :studentMetaUUID " +
            " AND student_contact_nos.status = :status " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or student_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countStudentContactNoRecordWithStatus(UUID studentMetaUUID, Boolean status, String key, String contactNo);

    @Query("select count(*) \n" +
            " from student_contact_nos \n" +
            " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where student_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and  student_contact_nos.student_meta_uuid= :studentMetaUUID " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or student_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countStudentContactNoRecordWithoutStatus(UUID studentMetaUUID, String key, String contactNo);


    /**
     * Fetch All Records With and Without Status Filter
     **/
    @Query("select count(*) \n" +
            " from student_contact_nos \n" +
            " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where student_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " AND student_contact_nos.status = :status " +
            " and (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or student_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countAllRecordWithStatus(Boolean status, String key, String contactNo);

    /**
     * Fetch All Records Without Status Filter
     **/
    @Query("select count(*) \n" +
            " from student_contact_nos \n" +
            " left join contact_categories on student_contact_nos.contact_category_uuid=contact_categories.uuid \n" +
            " left join contact_types on student_contact_nos.contact_type_uuid=contact_types.uuid \n" +
            " where student_contact_nos.deleted_at IS NULL \n" +
            " and  contact_categories.deleted_at IS NULL \n" +
            " and  contact_types.deleted_at IS NULL \n" +
            " and (concat_ws('|',contact_categories.name,contact_types.name,student_contact_nos.contact_no) ILIKE concat('%',:key,'%') \n" +
            " or student_contact_nos.contact_no ILIKE concat('%',:contactNo,'%')) ")
    Mono<Long> countAllRecordWithoutStatus(String key, String contactNo);

}
