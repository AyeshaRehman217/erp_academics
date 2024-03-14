package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloPloDto;

import java.util.UUID;

// This interface will extend in Slave CLO-PLO Repository
public interface SlaveCustomCloPloRepository {

    Flux<SlaveCloPloDto> indexWithDepartmentFilter(UUID departmentUUID, String key, String dp, String d, Integer size, Long page);

    Flux<SlaveCloPloDto> indexWithDepartmentAndStatusFilter(UUID departmentUUID, Boolean status, String key, String dp, String d, Integer size, Long page);

    Flux<SlaveCloPloDto> indexWithStatusFilter(Boolean status, String key, String dp, String d, Integer size, Long page);

    Flux<SlaveCloPloDto> index(String key, String dp, String d, Integer size, Long page);

    Mono<SlaveCloPloDto> showByUUID(UUID UUID);
}
