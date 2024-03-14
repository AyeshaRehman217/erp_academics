package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto;

// This interface wil extends in Slave Teacher-spouse-repository
public interface SlaveCustomTeacherSpouseTeacherSpouseProfileContactNoFacadeRepository {

    Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


