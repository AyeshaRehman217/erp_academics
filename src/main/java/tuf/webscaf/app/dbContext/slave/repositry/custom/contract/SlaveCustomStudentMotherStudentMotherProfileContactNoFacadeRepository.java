package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;

// This interface wil extends in Slave Student-mother-repository
public interface SlaveCustomStudentMotherStudentMotherProfileContactNoFacadeRepository {

    Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


