package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentChildStudentChildProfileContactNoFacadeDto;

// This interface wil extends in Slave Student-child-repository
public interface SlaveCustomStudentChildStudentChildProfileContactNoFacadeRepository {

    Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


