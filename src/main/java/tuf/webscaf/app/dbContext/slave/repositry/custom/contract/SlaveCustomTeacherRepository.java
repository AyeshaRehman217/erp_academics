package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;

import java.util.UUID;

// This interface wil extends in Slave Teacher Repository
public interface SlaveCustomTeacherRepository {

    //fetch All records with status and without status filter
    Flux<SlaveTeacherDto> indexWithoutStatus(String key, String employeeCode, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherDto> indexWithStatus(Boolean status, String key, String employeeCode, String dp, String d, Integer size, Long page);

    //fetch All records with status and without status filter
    Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> indexTeacherAndTeacherProfileAndContactNoWithoutStatus(String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> indexTeacherAndTeacherProfileAndContactNoWithStatus(Boolean status, String employeeCode, String firstName, String lastName, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page);

    Mono<SlaveTeacherDto> showByUuid(UUID uuid);

}
