package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloPeoPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;

import java.util.UUID;

/**
 * This Custom Interface will extend in slave PEO's Repository
 **/
public interface SlaveCustomPloPeoPvtRepository {
    /**
     * Un-mapped peo's list against plo uuid with and without status filter
     **/
    Flux<SlavePeoDto> showUnMappedPloPeoList(UUID ploUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> showUnMappedPloPeoListWithStatus(UUID ploUUID, Boolean status,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Un-mapped peo's list against plo uuid with and without status and Department filter
     **/
    Flux<SlavePeoDto> showUnMappedPloPeoListAgainstDepartment(UUID departmentUUID, UUID ploUUID,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> showUnMappedPloPeoListWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, Boolean status,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Mapped peo's list against plo uuid with and without status filter
     **/
    Flux<SlavePeoDto> showMappedPloPeoList(UUID ploUUID,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> showMappedPloPeoListWithStatus(UUID ploUUID, Boolean status,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    /**
     * Mapped peo's list against plo uuid with and without status filter
     **/
    Flux<SlavePeoDto> showMappedPloPeoListAgainstDepartment(UUID departmentUUID, UUID ploUUID,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePeoDto> showMappedPloPeoListWithStatusAndDepartment(UUID departmentUUID, UUID ploUUID, Boolean status,String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlavePloPeoPvtDto> index(String key, String dp, String d, Integer size, Long page);

    Flux<SlavePloPeoPvtDto> indexWithDepartment(UUID departmentUUID, String key, String dp, String d, Integer size, Long page);


}
