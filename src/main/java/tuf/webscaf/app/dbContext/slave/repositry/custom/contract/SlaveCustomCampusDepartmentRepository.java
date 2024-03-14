package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCampusDepartmentDto;

import java.util.UUID;

// This interface wil extends in Slave Campus Department Repository
public interface SlaveCustomCampusDepartmentRepository {

    Flux<SlaveCampusDepartmentDto> campusDepartmentIndex(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithStatus(String name, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithCampusAndStatus(UUID campusUUID, String name, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveCampusDepartmentDto> campusDepartmentIndexWithCampus(UUID campusUUID, String name, String dp, String d, Integer size, Long page);

    Mono<SlaveCampusDepartmentDto> campusDepartmentShow(UUID uuid);

}
