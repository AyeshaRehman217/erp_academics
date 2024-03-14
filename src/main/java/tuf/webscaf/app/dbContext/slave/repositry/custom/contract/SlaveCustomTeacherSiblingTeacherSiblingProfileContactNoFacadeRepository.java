package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto;

// This interface wil extends in Slave Teacher-sibling-repository
public interface SlaveCustomTeacherSiblingTeacherSiblingProfileContactNoFacadeRepository {

    Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> indexWithoutStatus(String name, String nic, String dp, String d, Integer size, Long page);

    Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> indexWithStatus(String name, String nic, Boolean status, String dp, String d, Integer size, Long page);


}


