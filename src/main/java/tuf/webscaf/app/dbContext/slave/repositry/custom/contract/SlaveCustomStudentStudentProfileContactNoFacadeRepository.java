package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;

// This interface wil extends in Slave Student Repository
public interface SlaveCustomStudentStudentProfileContactNoFacadeRepository {

    Flux<SlaveStudentStudentProfileContactNoFacadeDto> indexWithoutStatus(String firstname, String lastname, String studentId, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentStudentProfileContactNoFacadeDto> indexWithStatus(String firstname, String lastname, String studentId, String nic, Boolean status, String dp, String d, Integer size, Long page);


}
