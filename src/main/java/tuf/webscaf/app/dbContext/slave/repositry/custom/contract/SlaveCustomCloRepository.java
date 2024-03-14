package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;

import java.util.UUID;

// This interface will extend in Slave CLO Repository
public interface SlaveCustomCloRepository {

    /**
     * Fetch All Clo Records Against Department & Emphasis Level (With & Without Status Filter)
     **/
    Flux<SlaveCloDto> indexRecordsAgainstDepartmentAndEmphasisLevelWithStatus(Boolean status, UUID departmentUUID, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> indexRecordsAgainstDepartmentAndEmphasisLevelWithoutStatus(UUID departmentUUID, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Fetch All Clo Records Against Department (With & Without Status Filter)
     **/
    Flux<SlaveCloDto> indexRecordsAgainstDepartmentWithStatus(Boolean status, UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> indexRecordsAgainstDepartmentWithoutStatus(UUID departmentUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Fetch All Clo Records Against Emphasis level (With & Without Status Filter)
     **/
    Flux<SlaveCloDto> indexRecordsAgainstEmphasisLevelWithStatus(Boolean status, UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> indexRecordsAgainstEmphasisLevelWithoutStatus(UUID emphasisLevelUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Fetch All Clo Records Against Emphasis level (With & Without Status Filter)
     **/
    Flux<SlaveCloDto> indexRecordsWithStatus(Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> indexRecordWithoutStatus(String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Show Clo Records Against Emphasis level (With & Without Status Filter)
     **/
    Mono<SlaveCloDto> showCloRecords(UUID cloUUID);

}
