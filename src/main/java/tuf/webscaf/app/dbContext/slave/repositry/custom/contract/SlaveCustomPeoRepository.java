package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;

import java.util.UUID;

// This interface will extend in Slave Peo Repository
public interface SlaveCustomPeoRepository {

    /**
     * Fetch All Clo Records Against Department (With & Without Status Filter)
     **/
    Flux<SlavePeoDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);


    /**
     * Fetch All Clo Records Against Emphasis level (With & Without Status Filter)
     **/
    Flux<SlavePeoDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page);


    /**
     * Show Clo Records Against Emphasis level (With & Without Status Filter)
     **/
    Mono<SlavePeoDto> showPeoRecords(UUID peoUUID);

}
