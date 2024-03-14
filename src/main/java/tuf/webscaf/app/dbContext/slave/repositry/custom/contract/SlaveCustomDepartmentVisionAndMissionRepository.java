package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentVisionAndMissionDto;

// This interface wil extends in Course Offered Repository
public interface SlaveCustomDepartmentVisionAndMissionRepository {

    Flux<SlaveDepartmentVisionAndMissionDto> departmentVisionAndMissionIndex(String vision, String mission, String dp, String d, Integer size, Long page);

    Flux<SlaveDepartmentVisionAndMissionDto> departmentVisionAndMissionIndexWithStatus(String vision, String mission, Boolean status, String dp, String d, Integer size, Long page);
}