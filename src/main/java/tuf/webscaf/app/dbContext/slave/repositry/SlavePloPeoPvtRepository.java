package tuf.webscaf.app.dbContext.slave.repositry;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.entity.SlavePloPeoPvtEntity;
import tuf.webscaf.app.dbContext.slave.repositry.custom.contract.SlaveCustomPloPeoPvtRepository;

import java.util.UUID;

@Repository
public interface SlavePloPeoPvtRepository extends ReactiveCrudRepository<SlavePloPeoPvtEntity, Long>, SlaveCustomPloPeoPvtRepository {
    Mono<SlavePloPeoPvtEntity> findByIdAndDeletedAtIsNull(Long id);

    Mono<SlavePloPeoPvtEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    @Query("select count(*) from plo_peos_pvt \n" +
            "join peos on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "join plos on plos.uuid = plo_peos_pvt.plo_uuid \n" +
            "where peos.deleted_at is null\n" +
            "and plos.deleted_at is null \n" +
            "and plo_peos_pvt.deleted_at is null \n" +
            "and concat(plos.code,'|',peos.code) ILIKE concat('%',:key,'%') ")
    Mono<Long> countPloPeoRecords(String key);

    @Query("select count(*) from plo_peos_pvt \n" +
            "join peos on peos.uuid = plo_peos_pvt.peo_uuid\n" +
            "join plos on plos.uuid = plo_peos_pvt.plo_uuid \n" +
            "where plos.department_uuid = peos.department_uuid\n " +
            "and peos.department_uuid =:departmentUUID\n" +
            "and plos.department_uuid =:departmentUUID\n" +
            "and peos.deleted_at is null\n" +
            "and plos.deleted_at is null \n" +
            "and plo_peos_pvt.deleted_at is null \n" +
            "and concat(plos.code,'|',peos.code) ILIKE concat('%',:key,'%') ")
    Mono<Long> countPloPeoRecordsWithDepartment(UUID departmentUUID, String key);
}
