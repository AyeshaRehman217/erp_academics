package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloDto;

import java.util.UUID;

// This interface will extend in Slave Plo Repository
public interface SlaveCustomPloRepository {

    /**
     * Fetch All PLO Records Against Department (With & Without Status Filter)
     **/
    Flux<SlavePloDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePloDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);


    /**
     * Fetch All PLO Records Against Emphasis level (With & Without Status Filter)
     **/
    Flux<SlavePloDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePloDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page);


    /**
     * Show PLO Records
     **/
    Mono<SlavePloDto> showPloRecords(UUID ploUUID);

}
