package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;

// This interface wil extends in Slave Teacher-mother-repository
public interface SlaveCustomTeacherFatherTeacherFatherProfileContactNoFacadeRepository {

    Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


