package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherChildTeacherChildProfileContactNoFacadeDto;

// This interface wil extends in Slave Teacher-child-repository
public interface SlaveCustomTeacherChildTeacherChildProfileContactNoFacadeRepository {

    Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


