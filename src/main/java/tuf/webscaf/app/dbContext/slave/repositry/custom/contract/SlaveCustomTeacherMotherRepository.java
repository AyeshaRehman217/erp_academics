package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto;


// This interface wil extends in Slave Teacher Mother Repository
public interface SlaveCustomTeacherMotherRepository {

    //fetch All records with status and without status filter
    Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> indexTeacherMotherAndTeacherMotherProfileAndContactNoWithoutStatus(String name, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> indexTeacherMotherAndTeacherMotherProfileAndContactNoWithStatus(Boolean status, String name, String email, String telephoneNo, String nic, String dp, String d, Integer size, Long page);

}
