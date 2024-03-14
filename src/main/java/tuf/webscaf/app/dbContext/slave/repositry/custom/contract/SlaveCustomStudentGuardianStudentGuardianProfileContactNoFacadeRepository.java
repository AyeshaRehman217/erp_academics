package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto;

// This interface wil extends in Slave Student-spouse-repository
public interface SlaveCustomStudentGuardianStudentGuardianProfileContactNoFacadeRepository {

    Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


