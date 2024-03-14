package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto;

// This interface wil extends in Slave Student-sibling-repository
public interface SlaveCustomStudentSiblingStudentSiblingProfileContactNoFacadeRepository {

    Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


