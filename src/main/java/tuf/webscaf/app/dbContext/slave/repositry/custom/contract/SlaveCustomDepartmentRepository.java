package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentEntity;

import java.util.UUID;

public interface SlaveCustomDepartmentRepository {

    Flux<SlaveDepartmentEntity> showMappedDepartmentList(UUID departmentRankUUID, String catalogueName, String catalogueDescription, String dp, String d, Integer size, Long page);

    Flux<SlaveDepartmentEntity> showMappedDepartmentListWithStatus(UUID departmentRankUUID, String catalogueName, String catalogueDescription, Boolean status, String dp, String d, Integer size, Long page);
}
