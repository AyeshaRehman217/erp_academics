package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentFatherStudentFatherProfileContactNoFacadeDto;

// This interface wil extends in Slave Student-mother-repository
public interface SlaveCustomStudentFatherStudentFatherProfileContactNoFacadeRepository {

    Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


