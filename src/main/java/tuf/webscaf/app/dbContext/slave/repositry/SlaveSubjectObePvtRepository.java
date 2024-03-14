package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectObeCloPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomSubjectObeCloPvtRepository;

import java.util.UUID;

@Repository
public interface SlaveSubjectObePvtRepository extends ReactiveCrudRepository<SlaveSubjectObeCloPvtEntity, Long>, SlaveCustomSubjectObeCloPvtRepository {
    Mono<SlaveSubjectObeCloPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlaveSubjectObeCloPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("select count(*) from subject_obe_clos_pvt \n" +
            "join clos on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
            "join subject_obes on subject_obes.uuid = subject_obe_clos_pvt.subject_obe_uuid \n" +
            "where clos.deleted_at is null\n" +
            "and subject_obes.deleted_at is null \n" +
            "and subject_obe_clos_pvt.deleted_at is null \n" +
            "and concat(subject_obes.name,'|',clos.code) ILIKE concat('%',:key,'%') ")
    Mono<Long> countSubjectObeCloRecords(String key);

    @Query("select count(*) from subject_obe_clos_pvt \n" +
            "join clos on clos.uuid = subject_obe_clos_pvt.clo_uuid\n" +
            "join subject_obes on subject_obes.uuid = subject_obe_clos_pvt.subject_obe_uuid \n" +
            "where clos.department_uuid =:departmentUUID\n" +
            "and clos.deleted_at is null\n" +
            "and subject_obes.deleted_at is null \n" +
            "and subject_obe_clos_pvt.deleted_at is null \n" +
            "and concat(subject_obes.name,'|',clos.code) ILIKE concat('%',:key,'%') ")
    Mono<Long> countSubjectObeCloRecordsWithDepartment(UUID departmentUUID, String key);
}
